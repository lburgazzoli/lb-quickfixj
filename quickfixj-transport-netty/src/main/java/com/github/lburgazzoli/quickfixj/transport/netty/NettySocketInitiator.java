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
package com.github.lburgazzoli.quickfixj.transport.netty;

import com.github.lburgazzoli.quickfixj.transport.AbstractTransport;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionID;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class NettySocketInitiator extends AbstractTransport implements INettyStateHandler {
    private static Logger LOGGER =
        LoggerFactory.getLogger(NettySocketInitiator.class);

    private Bootstrap m_boot;

    /**
     * c-tor
     *
     * @param session
     */
    public NettySocketInitiator(FIXSessionHelper session) {
        super(session);

        m_boot = null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    @Override
    public void connect() {
        try {
            m_boot = new Bootstrap();
            m_boot.group(new NioEventLoopGroup());
            m_boot.channel(NioSocketChannel.class);
            m_boot.option(ChannelOption.SO_KEEPALIVE, true);
            m_boot.option(ChannelOption.TCP_NODELAY, true);
            m_boot.option(ChannelOption.ALLOCATOR,new PooledByteBufAllocator(true));
            m_boot.handler(new NettyChannelInitializer(this,getHelper(), FIXSessionType.INITIATOR));

            SessionID sid  = getHelper().getSession().getSessionID();
            String    host = getHelper().getSettings().getString(sid, "SocketConnectHost");
            int       port = getHelper().getSettings().getInt(sid, "SocketConnectPort");

            m_boot.remoteAddress(new InetSocketAddress(host,port));

            if(!isRunning()) {
                setRunning(true);
                doConnect();
            }
        } catch(Exception e) {
            LOGGER.warn("Exception", e);
            setRunning(false);
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if(m_boot != null) {
            m_boot.group().shutdownGracefully();
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private void doConnect() {
        ChannelFuture future = m_boot.connect();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if(future.isDone() && future.isSuccess()) {
                    setChannel(new NettyChannel(future.channel()));
                } else if(!future.isSuccess() && !future.isCancelled()) {
                    LOGGER.warn("Error", future.cause());
                    doReconnect();
                }
            }
        });
    }

    private void doReconnect() {
        if(isRunning() && m_boot != null) {
            Runnable task = new Runnable() {
                public void run() {
                    doConnect();
                }
            };

            m_boot.group().schedule(task,5,TimeUnit.SECONDS);
       }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void onConnect(Channel channel) {
    }

    @Override
    public void onDisconnect(Channel channel) {
        doReconnect();
    }
}
