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
package com.github.lburgazzoli.quickfixj.transport;

import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public abstract class AbstractTransport implements ITransport {

    private static Logger LOGGER =
        LoggerFactory.getLogger(AbstractTransport.class);

    private final FIXSessionHelper m_helper;
    private final AtomicBoolean m_running;

    private ITransportChannel m_channel;

    /**
     * c-tor
     *
     * @param helper
     */
    public AbstractTransport(FIXSessionHelper helper) {
        m_channel = null;
        m_running = new AtomicBoolean(false);

        m_helper = helper;
        m_helper.getSession().setResponder(this);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    @Override
    public FIXSessionHelper getHelper() {
        return m_helper;
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
        LOGGER.debug("Disconnect isRunning={}, channel={}",isRunning(),m_channel);

        if(isRunning()) {
            setRunning(false);
        }

        if(m_channel != null) {
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

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    protected IFIXContext getContext() {
        return m_helper.getContext();
    }

    /**
     *
     * @return
     */
    protected Session getSession() {
        return m_helper.getSession();
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
}
