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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public abstract class AbstractTransportSupport implements ITransportSupport {

    private static Logger LOGGER =
        LoggerFactory.getLogger(AbstractTransportSupport.class);

    private final FIXRuntime m_runtime;
    private final FIXSession m_session;
    private final AtomicBoolean m_running;

    private Channel m_channel;

    /**
     * c-tor
     *
     * @param runtime
     */
    public AbstractTransportSupport(FIXRuntime runtime,FIXSession session) {
        m_channel = null;
        m_runtime = runtime;
        m_running = new AtomicBoolean(false);

        m_session = session;
        m_session.getSession().setResponder(this);
    }

    /**
     *
     * @return
     */
    protected FIXRuntime getRuntime() {
        return m_runtime;
    }

    /**
     *
     * @return
     */
    protected FIXSession getSession() {
        return m_session;
    }

    /**
     *
     * @return
     */
    protected boolean isRunning() {
        return m_running.get();
    }

    /**
     *
     * @param running
     */
    protected void setRunning(boolean running) {
        m_running.set(running);
    }

    /**
     *
     * @return
     */
    protected Channel getChanngel() {
        return m_channel;
    }

    /**
     *
     * @param channel
     */
    protected void setChanngel(Channel channel) {
        m_channel = channel;
    }

    /**
     *
     * @param data
     * @return
     */
    @Override
    public boolean send(String data) {
        if(m_channel != null) {
            ChannelFuture future = m_channel.write(data);
            future.awaitUninterruptibly();

            if (!future.isSuccess()) {
                LOGGER.warn("Error sending message");
            }

            return future.isSuccess();
        }

        return false;
    }

    /**
     *
     */
    @Override
    public void disconnect() {
        if(m_channel != null && isRunning()) {
            setRunning(false);

            m_channel.disconnect().awaitUninterruptibly(5000L);
            m_channel.close().awaitUninterruptibly();
            m_channel = null;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getRemoteIPAddress() {
        return m_channel != null ? m_channel.getRemoteAddress().toString() : null;
    }
}
