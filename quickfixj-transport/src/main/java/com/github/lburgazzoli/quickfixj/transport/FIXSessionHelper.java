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

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import quickfix.field.MsgType;

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

    /**
     *
     * @param data
     * @throws Exception
     */
    public void processIncomingRawMessage(byte[] data) throws Exception{
        String    message   = new String(data);
        SessionID sessionid = MessageUtils.getReverseSessionID(message);

        if(validateSession(sessionid)) {
            try {
                Message msg = MessageUtils.parse(getSession(),message);

                if (getSession().hasResponder()) {
                    getSession().next(msg);
                } else {
                    try {
                        final String msgType = msg.getHeader().getString(MsgType.FIELD);
                        if (msgType.equals(MsgType.LOGOUT)) {
                            getSession().next(msg);
                        }
                    } catch (FieldNotFound ex) {
                        LOGGER.warn("FieldNotFound: {}",ex.getMessage());
                    }
                }
            } catch(InvalidMessage e) {
                try {
                    if(MsgType.LOGON.equals(MessageUtils.getMessageType(message))) {
                        LOGGER.error("Invalid LOGON message, disconnecting: " + e.getMessage());
                    } else {
                        LOGGER.error("Invalid message: " + e.getMessage());
                    }
                } catch(InvalidMessage e1) {
                    LOGGER.error("Invalid message: " + e1.getMessage());
                }
            }
            catch(Exception e)
            {
                LOGGER.warn("IOException,e");
            }
        } else {
            LOGGER.error("Disconnecting: received message for unknown session: " + message);
        }
    }

    /**
     *
     * @param sessionId
     * @return
     */
    protected boolean validateSession(SessionID sessionId) {
        return ObjectUtils.equals(sessionId,getSession().getSessionID());
    }
}
