package quickfix.netty;

import quickfix.Session;

/**
 *
 */
public class FIXSession {
    private final FIXRuntime m_runtime;
    private final Session m_session;

    /**
     * c-tor
     *
     * @param runtime
     * @param session
     */
    public FIXSession(FIXRuntime runtime,Session session) {
        m_runtime = runtime;
        m_session = session;
    }
}
