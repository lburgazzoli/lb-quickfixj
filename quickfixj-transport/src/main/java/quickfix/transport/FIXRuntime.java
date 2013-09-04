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

package quickfix.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.transport.util.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class FIXRuntime {
    private static Logger LOGGER =
        LoggerFactory.getLogger(FIXRuntime.class);

    private final ThreadFactory m_threadFactory;
    private final ScheduledExecutorService m_scheduler;

    /**
     * c-tor
     */
    public FIXRuntime() {
        m_threadFactory = new NamedThreadFactory("QFJ_Netty_Timer");
        m_scheduler = Executors.newSingleThreadScheduledExecutor(m_threadFactory);
    }

    /**
     * @param sessionId
     * @return
     */
    public Session getSession(SessionID sessionId) {
        return Session.lookupSession(sessionId);
    }

    /**
     *
     * @param sessionId
     * @param session
     */
    public void addSession(SessionID sessionId, Session session) {
        //TODO
    }

    /**
     *
     * @return
     */
    public ScheduledExecutorService getScheduler() {
        return m_scheduler;
    }
}
