/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Responder;
import quickfix.Session;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class FIXSocketInitiator extends FIXTransportSupport implements Runnable,Responder {
    private static Logger LOGGER =
        LoggerFactory.getLogger(FIXSocketInitiator.class);

    private Session m_session;
    private AtomicBoolean m_running;
    private String m_host;
    private int m_port;
    private Channel m_channel;

    /**
     * c-tor
     *
     * @param runtime
     * @param session
     * @param host
     * @param port
     */
    public FIXSocketInitiator(FIXRuntime runtime,Session session,String host, int port) {
        super(runtime);

        m_running = new AtomicBoolean(true);
        m_host    = host;
        m_port    = port;
        m_channel = null;

        m_session = session;
        m_session.setResponder(this);
    }

    /**
     *
     */
    public void stop() {
        m_running.set(false);
    }

    /**
     *
     */
    @Override
    public void run() {
        ChannelFactory factory =
            new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new FIXProtocolPipelineFactory(getRuntime(),m_session,FIXSessionType.INITIATOR));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        ChannelFuture future  = bootstrap.connect(new InetSocketAddress(m_host,m_port));
        m_channel = future.awaitUninterruptibly().getChannel();

        if (!future.isSuccess()) {
            LOGGER.warn("Error", future.getCause());
        } else {
            try {
                while(m_running.get()) {
                    try{ Thread.sleep(5000); } catch(Exception e) {}
                }
            } catch(Exception e) {
                LOGGER.warn("Error", e);
            }

            m_channel.close().awaitUninterruptibly();
        }

        bootstrap.releaseExternalResources();
    }

    /**
     *
     * @param data
     * @return
     */
    @Override
    public boolean send(String data) {
        ChannelFuture future = m_channel.write(data);
        if (!future.isSuccess()) {
            LOGGER.warn("Error sending message");
        }

        return future.isSuccess();
    }

    /**
     *
     */
    @Override
    public void disconnect() {
        m_channel.disconnect().awaitUninterruptibly(5000L);
        m_channel.close().awaitUninterruptibly();
    }

    /**
     *
     * @return
     */
    @Override
    public String getRemoteIPAddress() {
        return m_channel.getRemoteAddress().toString();
    }
}
