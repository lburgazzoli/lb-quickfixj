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

package quickfix.netty;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.netty.util.NamedThreadFactory;
import quickfix.netty.util.SessionTimerTask;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 *
 */
public class FIXRuntime {
    private static Logger LOGGER =
        LoggerFactory.getLogger(FIXRuntime.class);

    private final Map<Session,ScheduledFuture<?>> m_tasks;
    private final ScheduledExecutorService m_scheduler;

    /**
     * c-tor
     */
    public FIXRuntime() {
        m_scheduler = Executors.newSingleThreadScheduledExecutor(NamedThreadFactory.newWithName("QFJ_Netty_Timer"));
        m_tasks     = Maps.newConcurrentMap();
    }

    /**
     * @param sessionId
     * @return
     */
    public Session find(SessionID sessionId) {
        return Session.lookupSession(sessionId);
    }

    /**
     *
     * @param session
     */
    public void startSessionTimer(Session session) {
        m_tasks.put(session,SessionTimerTask.schedule(m_scheduler, session));
        LOGGER.info("SessionTimer started");
    }

    /**
     *
     * @param session
     */
    public void stopSessionTimer(Session session) {
        ScheduledFuture<?> future = m_tasks.get(session);
        if(future != null) {
             if(future.cancel(false)) {
                 LOGGER.info("SessionTimer canceled");
             }
        }
    }
}
