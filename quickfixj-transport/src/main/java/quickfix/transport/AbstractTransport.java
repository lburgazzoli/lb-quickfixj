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

package quickfix.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ext.IFIXContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public abstract class AbstractTransport implements ITransport {

    private static Logger LOGGER =
        LoggerFactory.getLogger(AbstractTransport.class);

    private final FIXSessionHelper m_session;
    private final AtomicBoolean m_running;

    private ITransportChannel m_channel;

    /**
     * c-tor
     *
     * @param session
     */
    public AbstractTransport(FIXSessionHelper session) {
        m_channel = null;
        m_running = new AtomicBoolean(false);

        m_session = session;
        m_session.getSession().setResponder(this);
    }

    /**
     *
     * @return
     */
    protected IFIXContext getRuntime() {
        return m_session.getContext();
    }

    /**
     *
     * @return
     */
    protected FIXSessionHelper getSession() {
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
    protected ITransportChannel getChannel() {
        return m_channel;
    }

    /**
     *
     * @param channel
     */
    protected void setChannel(ITransportChannel channel) {
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
            return m_channel.send(data);
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

            m_channel.disconnect();
            m_channel = null;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getRemoteIPAddress() {
        return m_channel != null ? m_channel.getRemoteIPAddress() : null;
    }
}
