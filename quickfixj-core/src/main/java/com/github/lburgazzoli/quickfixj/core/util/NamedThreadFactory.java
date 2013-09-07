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

package com.github.lburgazzoli.quickfixj.core.util;

import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String m_threadName;

    /**
     * c-tor
     *
     * @param threadName
     */
    public NamedThreadFactory(String threadName) {
        m_threadName = threadName;
    }

    /**
     *
     * @param runnable
     * @return
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable,m_threadName);
        thread.setDaemon(true);
        return thread;
    }
}
