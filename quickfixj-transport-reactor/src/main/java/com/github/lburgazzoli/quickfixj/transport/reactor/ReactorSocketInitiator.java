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

package com.github.lburgazzoli.quickfixj.transport.reactor;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import com.github.lburgazzoli.quickfixj.transport.AbstractTransport;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import reactor.event.Event;

/**
 *
 */
public class ReactorSocketInitiator extends AbstractTransport {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ReactorSocketInitiator.class);

    // *************************************************************************
    //
    // *************************************************************************

    private final ReactorChannelEvents.ConnectionUpConsumer m_consumerCnxUp =
        new ReactorChannelEvents.ConnectionUpConsumer() {
            @Override
            public void accept(final Event<ReactorChannelEvents.ConnectionUp> data) {
                LOGGER.debug("TCPEvents.ConnectionUp");
                onConnectionUp(data.getData());
            }
    };

    private final ReactorChannelEvents.ConnectionDownConsumer m_consumerCnxDown =
        new ReactorChannelEvents.ConnectionDownConsumer() {
            @Override
            public void accept(final Event<ReactorChannelEvents.ConnectionDown> data) {
                LOGGER.debug("TCPEvents.ConnectionDown");
                onConnectionDown(data.getData());
            }
    };

    private final ReactorChannelEvents.DataConsumer m_consumerData =
        new ReactorChannelEvents.DataConsumer() {
            @Override
            public void accept(final Event<ReactorChannelEvents.Data> data) {
                LOGGER.debug("TCPEvents.Data");
                onData(data.getData());
            }
    };

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * c-tor
     *
     * @param session
     */
    public ReactorSocketInitiator(FIXSessionHelper session) {
        super(session);
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void run() {
        try {
            SessionID sid  = getHelper().getSession().getSessionID();
            String    host = getHelper().getSettings().getString(sid, "SocketConnectHost");
            int       port = getHelper().getSettings().getInt(sid, "SocketConnectPort");

            ReactorChannel channel = new ReactorChannel(ReactorConstants.ENV);
            channel.on(ReactorChannelEvents.ConnectionUp.class,m_consumerCnxUp);
            channel.on(ReactorChannelEvents.ConnectionDown.class,m_consumerCnxDown);
            channel.on(ReactorChannelEvents.Data.class,m_consumerData);
            channel.connect(host,port);

            if(!isRunning()) {
                setRunning(true);
                setChannel(channel);
            }
        } catch(Exception e) {
            LOGGER.warn("Exception", e);
            setRunning(false);
        }
    }

    /**
     *
     * @param data
     */
    protected void onConnectionUp(final ReactorChannelEvents.ConnectionUp data) {
        LOGGER.debug("onConnectionUp");
        getHelper().getSession().logon();
        getHelper().startSessionTimer();
    }

    /**
     *
     * @param data
     */
    protected void onConnectionDown(final ReactorChannelEvents.ConnectionDown data) {
        LOGGER.debug("onConnectionDown");
        getHelper().stopSessionTimer();
    }

    /**
     *
     * @param data
     */
    protected void onData(final ReactorChannelEvents.Data data) {
        String    message   = new String(data.get());
        SessionID sessionid = MessageUtils.getReverseSessionID(message);
        Session   session   = getRuntime().getSession(sessionid);

        if(ObjectUtils.equals(sessionid, session.getSessionID())) {
            try {
                Message msg = MessageUtils.parse(session,message);

                if (session.hasResponder()) {
                    session.next(msg);
                } else {
                    try {
                        final String msgType = msg.getHeader().getString(MsgType.FIELD);
                        if (msgType.equals(MsgType.LOGOUT)) {
                            session.next(msg);
                        }
                    } catch (FieldNotFound ex) {
                        LOGGER.warn("FieldNotFound: {}",ex.getMessage());
                    }
                }
            } catch(InvalidMessage e) {
                try {
                    if(MsgType.LOGON.equals(MessageUtils.getMessageType(message))) {
                        LOGGER.error("Invalid LOGON message, disconnecting: " + e.getMessage());
                        stop();
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
        }
        else
        {
            LOGGER.error("Disconnecting: received message for unknown session: " + message);
            stop();
        }
    }
}
