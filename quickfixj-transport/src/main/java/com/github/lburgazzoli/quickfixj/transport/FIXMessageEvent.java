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
