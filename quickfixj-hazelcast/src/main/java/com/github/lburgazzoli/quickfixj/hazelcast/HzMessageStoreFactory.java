/*
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
 */
package com.github.lburgazzoli.quickfixj.hazelcast;

import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;
import quickfix.MessageStore;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;

import java.util.Map;

/**
 *
 */
public class HzMessageStoreFactory implements MessageStoreFactory {
    private final Map<SessionID,MessageStore> m_stores;
    private final HazelcastInstance m_hz;

    /**
     * c-tor
     */
    public HzMessageStoreFactory(HazelcastInstance hz) {
        m_stores = Maps.newHashMap();
        m_hz = hz;
    }

    @Override
    public synchronized MessageStore create(SessionID sessionID) {
        if(!m_stores.containsKey(sessionID)) {
            m_stores.put(sessionID,new HzMessageStore(sessionID,m_hz));
        }

        return m_stores.get(sessionID);
    }
}
