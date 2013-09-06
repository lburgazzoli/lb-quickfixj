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

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RuntimeError;
import quickfix.ScreenLogFactory;
import quickfix.SessionFactory;
import quickfix.SessionSettings;
import quickfix.ext.IFIXContext;
import com.github.lburgazzoli.quickfixj.transport.mina.initiator.AbstractSocketInitiator;

/**
 * Initiates connections and uses a separate thread per session to process messages.
 */
public class ThreadedSocketInitiator extends AbstractSocketInitiator {
    private final ThreadPerSessionEventHandlingStrategy eventHandlingStrategy =
        new ThreadPerSessionEventHandlingStrategy(this);

    public ThreadedSocketInitiator(final IFIXContext context,Application application,
            MessageStoreFactory messageStoreFactory, SessionSettings settings,
            LogFactory logFactory, MessageFactory messageFactory) throws ConfigError {
        super(context,application, messageStoreFactory, settings, logFactory, messageFactory);
    }

    public ThreadedSocketInitiator(final IFIXContext context,Application application,
            MessageStoreFactory messageStoreFactory, SessionSettings settings,
            MessageFactory messageFactory) throws ConfigError {
        super(context,application, messageStoreFactory, settings, new ScreenLogFactory(settings),
                messageFactory);
    }

    public ThreadedSocketInitiator(final IFIXContext context,SessionFactory sessionFactory, SessionSettings settings)
            throws ConfigError {
        super(context,settings, sessionFactory);
    }

    public void start() throws ConfigError, RuntimeError {
        createSessionInitiators();
        startInitiators();
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean forceDisconnect) {
        logoutAllSessions(forceDisconnect);
        stopSessionTimer();
        if (!forceDisconnect) {
            waitForLogout();
        }
        eventHandlingStrategy.stopDispatcherThreads();
        getContext().removeSessions(getSessions());
    }

    public void block() throws ConfigError, RuntimeError {
        throw new UnsupportedOperationException("Blocking not supported: " + getClass());
    }

    @Override
    protected EventHandlingStrategy getEventHandlingStrategy() {
        return eventHandlingStrategy;
    }

}
