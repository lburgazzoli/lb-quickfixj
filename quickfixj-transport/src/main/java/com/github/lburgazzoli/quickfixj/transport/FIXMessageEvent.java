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
import quickfix.FieldNotFound;
import quickfix.LogUtil;
import quickfix.Message;
import quickfix.Session;
import quickfix.field.MsgType;


/**
 *
 */
public class FIXMessageEvent {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(FIXMessageEvent.class);

    private final Session m_session;
    private final Message m_message;

    /**
     * c-tor
     *
     * @param session
     * @param message
     */
    public FIXMessageEvent(Session session, Message message) {
        m_session = session;
        m_message = message;
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
    public Message getMessage() {
        return m_message;
    }

    /**
     *
     */
    public void processMessage() {
        try {
            if (m_session.hasResponder()) {
                m_session.next(m_message);
            } else {
                try {
                    final String msgType = m_message.getHeader().getString(MsgType.FIELD);
                    if (msgType.equals(MsgType.LOGOUT)) {
                        m_session.next(m_message);
                    }
                } catch (FieldNotFound ex) {
                }
            }
        } catch (Throwable e) {
            LogUtil.logThrowable(m_session,e.getMessage(), e);
        }
    }
}
