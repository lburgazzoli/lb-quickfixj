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
package com.github.lburgazzoli.quickfixj.transport.reactor;

import com.github.lburgazzoli.quickfixj.transport.AbstractTransport;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionID;
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
    public void connect() {
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
        try {
            getHelper().processIncomingRawMessage(data.get());
        } catch(Exception e) {
            LOGGER.error("Disconnecting: received message for unknown session",e);
            disconnect();
        }
    }
}
