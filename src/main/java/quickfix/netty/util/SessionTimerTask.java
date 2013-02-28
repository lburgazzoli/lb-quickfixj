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

package quickfix.netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SessionTimerTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTimerTask.class);

    private final Session m_session;

    /**
     * c-tor
     *
     * @param session
     */
    public SessionTimerTask(Session session) {
        m_session = session;
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            m_session.next();
        } catch (IOException e) {
            LOGGER.error("{} : Error in session timer processing ({})",m_session.getSessionID(),e.getMessage());
        }
    }

    /**
     *
     * @param scheduler
     * @param session
     * @return
     */
    public static final ScheduledFuture<?> schedule(ScheduledExecutorService scheduler,Session session) {
        return scheduler.scheduleAtFixedRate(new SessionTimerTask(session),0, 1000L, TimeUnit.MILLISECONDS);
    }
}
