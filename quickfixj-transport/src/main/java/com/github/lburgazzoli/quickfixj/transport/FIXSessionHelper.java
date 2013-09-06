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

package com.github.lburgazzoli.quickfixj.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;
import quickfix.SessionSettings;
import quickfix.ext.IFIXContext;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class FIXSessionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FIXSessionHelper.class);

    /**
     *
     */
    private final Runnable m_task = new Runnable() {
        public void run() {
            try {
                m_session.next();
            } catch (IOException e) {
                LOGGER.error("{} : Error in session timer processing ({})",
                    m_session.getSessionID(),
                    e.getMessage());
            }
        }
    };

    private final Session m_session;
    private final SessionSettings m_settings;

    private ScheduledFuture<?> m_taskFuture;

    /**
     * c-tor
     *
     * @param session
     * @param settings
     */
    public FIXSessionHelper(Session session, SessionSettings settings) {
        m_session    = session;
        m_settings   = settings;
        m_taskFuture = null;
    }

    /**
     *
     * @return
     */
    public IFIXContext getContext() {
        return m_session.getContext();
    }

    /**
     *
     * @return
     */
    public Session getSession() {
        return m_session;
    }

    /**
     *
     * @return
     */
    public SessionSettings getSettings() {
        return m_settings;
    }

    /**
     *
     */
    public void startSessionTimer() {
        if(m_taskFuture == null) {
            m_taskFuture = getContext().getScheduler().scheduleAtFixedRate(
                m_task, 0, 1000L, TimeUnit.MILLISECONDS);

            LOGGER.info("SessionTimer started");
        }
    }

    /**
     *
     */
    public void stopSessionTimer() {
        if(m_taskFuture != null) {
            if(m_taskFuture.cancel(false)) {
                LOGGER.info("SessionTimer canceled");
            }

            m_taskFuture = null;
        }
    }
}
