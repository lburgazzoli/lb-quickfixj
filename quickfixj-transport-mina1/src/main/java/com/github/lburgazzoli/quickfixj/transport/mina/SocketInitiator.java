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

package com.github.lburgazzoli.quickfixj.transport.mina;

import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import com.github.lburgazzoli.quickfixj.transport.mina.initiator.AbstractSocketInitiator;
import quickfix.*;

/**
 * Initiates connections and uses a single thread to process messages for all
 * sessions.
 */
public class SocketInitiator extends AbstractSocketInitiator {
    private Boolean isStarted = Boolean.FALSE;
    private final Object lock = new Object();
    private SingleThreadedEventHandlingStrategy eventHandlingStrategy =
    	new SingleThreadedEventHandlingStrategy(this);

    public SocketInitiator(final IFIXContext context,Application application, MessageStoreFactory messageStoreFactory,
            SessionSettings settings, MessageFactory messageFactory)
            throws ConfigError {
        super(context,application, messageStoreFactory, settings, messageFactory);
        if (settings == null) {
            throw new ConfigError("no settings");
        }
    }

    public SocketInitiator(final IFIXContext context,SessionFactory sessionFactory, SessionSettings settings) throws ConfigError {
        super(context,settings, sessionFactory);
    }

    public void block() throws ConfigError, RuntimeError {
        initialize();
        eventHandlingStrategy.block();
    }

    public void start() throws ConfigError, RuntimeError {
        initialize();
        eventHandlingStrategy.blockInThread();
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean forceDisconnect) {
        try {
            eventHandlingStrategy.stopHandlingMessages();
            logoutAllSessions(forceDisconnect);
            stopInitiators();
        } finally {
            getContext().removeSessions(getSessions());
            isStarted = Boolean.FALSE;
        }
    }

    private void initialize() throws ConfigError {
        synchronized (lock) {
            if (isStarted.equals(Boolean.FALSE)) {
                createSessionInitiators();
                for (Session session : getSessionMap().values()) {
                    getContext().addSession(session);
                }
                startInitiators();
                isStarted = Boolean.TRUE;
            }
        }
    }

    @Override
    protected EventHandlingStrategy getEventHandlingStrategy() {
        return eventHandlingStrategy;
    }

}