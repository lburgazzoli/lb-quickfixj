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
import quickfix.SessionFactory;
import quickfix.SessionSettings;
import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import com.github.lburgazzoli.quickfixj.transport.mina.acceptor.AbstractSocketAcceptor;

/**
 * Accepts connections and uses a separate thread per session to process messages.
 */
public class ThreadedSocketAcceptor extends AbstractSocketAcceptor {
    private final ThreadPerSessionEventHandlingStrategy eventHandlingStrategy = new ThreadPerSessionEventHandlingStrategy(
            this);

    public ThreadedSocketAcceptor(final IFIXContext context,Application application, MessageStoreFactory messageStoreFactory,
            SessionSettings settings, LogFactory logFactory, MessageFactory messageFactory)
            throws ConfigError {
        super(context,application, messageStoreFactory, settings, logFactory, messageFactory);
    }

    public ThreadedSocketAcceptor(final IFIXContext context,Application application, MessageStoreFactory messageStoreFactory,
            SessionSettings settings, MessageFactory messageFactory) throws ConfigError {
        super(context,application, messageStoreFactory, settings, messageFactory);
    }

    public ThreadedSocketAcceptor(final IFIXContext context,SessionFactory sessionFactory, SessionSettings settings)
            throws ConfigError {
        super(context,settings, sessionFactory);
    }

    public void start() throws ConfigError, RuntimeError {
        startAcceptingConnections();
    }

    public void stop() {
        stop(false);
    }

    public void stop(boolean forceDisconnect) {
        stopAcceptingConnections();
        logoutAllSessions(forceDisconnect);
        stopSessionTimer();
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
