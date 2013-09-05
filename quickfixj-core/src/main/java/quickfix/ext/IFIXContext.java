package quickfix.ext;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public interface IFIXContext {

    // *************************************************************************
    //
    // *************************************************************************

    public ScheduledExecutorService getScheduler();

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param sessionId
     * @return
     */
    public Session getSession(
        SessionID sessionId);

    /**
     *
     * @param session
     */
    public void addSession(
        Session session);

    /**
     *
     * @param sessionId
     * @param session
     */
    public void addSession(
        SessionID sessionId,
        Session session);

    /**
     *
     * @param sessionId
     */
    public void removeSession(
        SessionID sessionId);

    /**
     * @param sessionIds
     */
    public void removeSessions(
        Collection<SessionID> sessionIds);

    /**
     * Determine if a session exists with the given ID.
     * @param sessionID
     * @return true if session exists, false otherwise.
     */
    public boolean doesSessionExist(SessionID sessionID);

    /**
     * Return the session count.
     * @return the number of sessions
     */
    public int numSessions();

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param message
     * @return
     * @throws SessionNotFound
     */
    public boolean sendToTarget(
        Message message)
        throws SessionNotFound;

    /**
     * Send a message to the session specified in the message's target
     * identifiers. The session qualifier is used to distinguish sessions with
     * the same target identifiers.
     *
     * @param message   a FIX message
     * @param qualifier a session qualifier
     * @return true is send was successful, false otherwise
     * @throws SessionNotFound if session could not be located
     */
    public boolean sendToTarget(
        Message message,
        String qualifier)
        throws SessionNotFound;

    /**
     * Send a message to the session specified by the provided target company
     * ID. The sender company ID is provided as an argument rather than from the
     * message.
     *
     * @param message      a FIX message
     * @param senderCompID the sender's company ID
     * @param targetCompID the target's company ID
     * @return true is send was successful, false otherwise
     * @throws SessionNotFound if session could not be located
     */
    public boolean sendToTarget(
        Message message,
        String senderCompID,
        String targetCompID)
        throws SessionNotFound;
    /**
     * Send a message to the session specified by the provided target company
     * ID. The sender company ID is provided as an argument rather than from the
     * message. The session qualifier is used to distinguish sessions with the
     * same target identifiers.
     *
     * @param message      a FIX message
     * @param senderCompID the sender's company ID
     * @param targetCompID the target's company ID
     * @param qualifier    a session qualifier
     * @return true is send was successful, false otherwise
     * @throws SessionNotFound if session could not be located
     */
    public boolean sendToTarget(
        Message message,
        String senderCompID,
        String targetCompID,
        String qualifier)
        throws SessionNotFound;

    /**
     * Send a message to the session specified by the provided session ID.
     *
     * @param message   a FIX message
     * @param sessionID the target SessionID
     * @return true is send was successful, false otherwise
     * @throws SessionNotFound if session could not be located
     */
    public boolean sendToTarget(
        Message message,
        SessionID sessionID)
        throws SessionNotFound;
}
