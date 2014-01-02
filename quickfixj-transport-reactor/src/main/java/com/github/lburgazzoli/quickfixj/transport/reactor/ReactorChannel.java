/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.quickfixj.transport.reactor;

import com.github.lburgazzoli.quickfixj.transport.ITransportChannel;
import com.github.lburgazzoli.quickfixj.transport.netty.codec.NettyMessageDecoder;
import com.github.lburgazzoli.quickfixj.transport.netty.codec.NettyMessageEncoder;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.composable.Promise;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.event.registry.Registration;
import reactor.event.selector.Selectors;
import reactor.function.Consumer;
import reactor.tcp.Reconnect;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpConnection;
import reactor.tcp.netty.NettyClientSocketOptions;
import reactor.tcp.netty.NettyTcpClient;
import reactor.tcp.spec.TcpClientSpec;
import reactor.tuple.Tuple;
import reactor.tuple.Tuple2;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ReactorChannel implements ITransportChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorChannel.class);

    private final Reconnect m_tcpRecx;
    private final Environment m_tcpEnv;
    private final AtomicBoolean m_running;
    private final ConnectionConsumer m_consumerCnx;
    private final ExceptionConsumer m_consumerEx;
    private final DataConsumer m_consumerData;
    private final CloseHandler m_closeHandler;
    private final Reactor m_reactor;

    private TcpClient<byte[],byte[]> m_tcpClient;
    private TcpConnection<byte[],byte[]> m_tcpCnx;

    /**
     *
     * @param env
     */
    public ReactorChannel(Environment env) {
        m_running          = new AtomicBoolean(false);
        m_tcpEnv           = env;
        m_tcpClient        = null;
        m_tcpCnx           = null;
        m_tcpRecx          = new ReconnectPolicy();
        m_consumerCnx      = new ConnectionConsumer();
        m_consumerEx       = new ExceptionConsumer();
        m_consumerData     = new DataConsumer();
        m_closeHandler     = new CloseHandler();
        m_reactor          = Reactors.reactor().env(env).dispatcher(Environment.RING_BUFFER).get();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param host
     * @param port
     */
    public ReactorChannel connect(String host,int port) {
        if(m_tcpClient == null) {

            final Consumer<ChannelPipeline> pipelineConsumer = new Consumer<ChannelPipeline>() {
                @Override
                public void accept(ChannelPipeline pipeline) {
                    pipeline.addLast("fix-decoder", new NettyMessageDecoder());
                    pipeline.addLast("fix-encoder", new NettyMessageEncoder());
                }
            };

            // To use Reactor's Codecs:
            //  .codec(new ReactorFrameCodec())
            m_tcpClient = new TcpClientSpec<byte[],byte[]>(NettyTcpClient.class)
                .env(m_tcpEnv)
                .dispatcher(Environment.RING_BUFFER)
                .options(
                    new NettyClientSocketOptions()
                        .pipelineConfigurer(pipelineConsumer)
                        .keepAlive(true)
                        .tcpNoDelay(true))
                .connect(host,port)
                .get();

            m_tcpClient.open(m_tcpRecx)
                .consume(m_consumerCnx)
                .when(Exception.class,m_consumerEx);
        }

        return this;
    }

    /**
     *
     * @param type
     * @param consumer
     * @return
     */
    public <T> Registration<Consumer<Event<T>>> on(
        Class<T> type,
        Consumer<Event<T>> consumer)
    {
        LOGGER.debug("Subscribe to {} with consumer {}",type,consumer);
        return m_reactor.on(Selectors.type(type),consumer);
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public boolean send(String data) {
        m_tcpCnx.out().accept(data.getBytes());
        return true;
    }

    @Override
    public boolean disconnect() {
        if(m_tcpClient != null && m_running.get()) {
            Promise<Void> p = m_tcpClient.close().onSuccess(new Consumer<Void>() {
                @Override
                public void accept(Void v) {
                    m_reactor.notify(
                        ReactorChannelEvents.ConnectionDown.class,
                        ReactorChannelEvents.ConnectionDown.wrap(m_tcpCnx));
                }
            });

            try {
                p.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.warn("InterruptedException",e);
            }

            m_tcpClient = null;
            m_running.set(false);
        }

        return true;
    }

    @Override
    public String getRemoteIPAddress() {
        return m_tcpCnx != null ? m_tcpCnx.remoteAddress().toString() : null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    private class ReconnectPolicy implements Reconnect {
        @Override
        public Tuple2<InetSocketAddress, Long> reconnect(
            InetSocketAddress currentAddress,int attempt) {
            return Tuple.of(currentAddress, 5L);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private final class ConnectionConsumer implements Consumer<TcpConnection<byte[],byte[]>> {
        @Override
        public void accept(TcpConnection<byte[],byte[]> cnx) {
            m_tcpCnx = cnx;
            m_tcpCnx.in()
                .consume(m_consumerData);
            m_tcpCnx.on()
                .close(m_closeHandler)
                .readIdle(ReadIdleHandler.IDLE_TIME,new ReadIdleHandler())
                .writeIdle(WriteIdleHandler.IDLE_TIME,new WriteIdleHandler());
            m_reactor.notify(
                ReactorChannelEvents.ConnectionUp.class,
                ReactorChannelEvents.ConnectionUp.wrap(m_tcpCnx));

            m_running.set(true);
        }
    }

    private final class DataConsumer implements Consumer<byte[]> {
        @Override
        public void accept(byte[] buffer) {
             m_reactor.notify(
                 ReactorChannelEvents.Data.class,
                 ReactorChannelEvents.Data.wrap(buffer));
        }
    }

    /**
     *
     */
    private final class ExceptionConsumer implements Consumer<Exception> {
        @Override
        public void accept(Exception e) {
            LOGGER.warn("Exception",e);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private final class CloseHandler implements Runnable {
        @Override
        public void run() {
            m_reactor.notify(
                ReactorChannelEvents.ConnectionDown.class,
                ReactorChannelEvents.ConnectionDown.wrap(m_tcpCnx));

            m_running.set(false);
        }
    }

    /**
     *
     */
    private final class ReadIdleHandler implements Runnable {
        public static final long IDLE_TIME = 1000 * 10;

        @Override
        public void run() {
            LOGGER.debug("ReadIdle ({})",IDLE_TIME);
        }
    }

    /**
     *
     */
    private final class WriteIdleHandler implements Runnable {
        public static final long IDLE_TIME =  1000 * 10;

        @Override
        public void run() {
            LOGGER.debug("WriteIdle ({})",IDLE_TIME);
        }
    }
}