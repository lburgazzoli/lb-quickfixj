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

package quickfix.ext;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionException;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.ext.util.NamedThreadFactory;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class FIXContext implements IFIXContext {
    private static Logger LOGGER =
        LoggerFactory.getLogger(FIXContext.class);

    private final ThreadFactory m_threadFactory;
    private final ScheduledExecutorService m_scheduler;
    private final ConcurrentMap<SessionID, Session> m_sessions;

    /**
     * c-tor
     */
    public FIXContext() {
        m_threadFactory = new NamedThreadFactory("QFJ_Timer");
        m_scheduler = Executors.newSingleThreadScheduledExecutor(m_threadFactory);
        m_sessions = Maps.newConcurrentMap();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    @Override
    public ScheduledExecutorService getScheduler() {
        return m_scheduler;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public Set<SessionID> getSessionIDs() {
        return m_sessions.keySet();
    }

    @Override
    public Session getSession(SessionID sessionId) {
        return m_sessions.get(sessionId);
    }

    @Override
    public void addSession(Session session) {
        addSession(session.getSessionID(), session);
    }

    @Override
    public void addSession(SessionID sessionId, Session session) {
        m_sessions.put(sessionId, session);
    }

    @Override
    public void removeSessions(Collection<SessionID> sessionIds) {
        for (final SessionID sessionId : sessionIds) {
            removeSession(sessionId);
        }
    }

    @Override
    public void removeSession(SessionID sessionId) {
        final Session session = m_sessions.remove(sessionId);
        if(session != null) {
            try {
                session.close();
            } catch (final IOException e) {
                LOGGER.error("Failed to close session", e);
            }
        }
    }

    /**
     * Determine if a session exists with the given ID.
     * @param sessionID
     * @return true if session exists, false otherwise.
     */
    public boolean doesSessionExist(SessionID sessionID) {
        return m_sessions.containsKey(sessionID);
    }

    /**
     * Return the session count.
     * @return the number of sessions
     */
    public int numSessions() {
        return m_sessions.size();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public boolean sendToTarget(
        Message message)
        throws SessionNotFound
    {
        return sendToTarget(message, "");
    }

    @Override
    public boolean sendToTarget(
        Message message,
        String qualifier)
        throws SessionNotFound
    {
        try
        {
            return sendToTarget(message,
                message.getHeader().getString(SenderCompID.FIELD),
                message.getHeader().getString(TargetCompID.FIELD),
                qualifier);
        }
        catch (final FieldNotFound e)
        {
            throw new SessionNotFound("missing sender or target company ID");
        }
    }

    @Override
    public boolean sendToTarget(
        Message message,
        String senderCompID,
        String targetCompID)
        throws SessionNotFound
    {
        return sendToTarget(
            message,
            senderCompID,
            targetCompID,
            StringUtils.EMPTY);
    }

    @Override
    public boolean sendToTarget(
        Message message,
        String senderCompID,
        String targetCompID,
        String qualifier)
        throws SessionNotFound
    {
        try
        {
            return sendToTarget(message,
                new SessionID(
                    message.getHeader().getString(BeginString.FIELD),
                    senderCompID,
                    targetCompID,
                    qualifier)
            );
        }
        catch (final SessionNotFound e)
        {
            throw e;
        }
        catch (final Exception e)
        {
            throw new SessionException(e);
        }
    }

    @Override
    public boolean sendToTarget(
        Message message,
        SessionID sessionID)
        throws SessionNotFound
    {
        final Session session = getSession(sessionID);
        if (session == null)
        {
            throw new SessionNotFound();
        }

        message.setSessionID(sessionID);
        return session.send(message);
    }

}
