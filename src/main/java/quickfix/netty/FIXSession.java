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

    /**
     *
     * @return
     */
    public FIXRuntime getRuntime() {
        return m_runtime;
    }

    /**
     *
     * @return
     */
    public Session getSession() {
        return m_session;
    }
}
