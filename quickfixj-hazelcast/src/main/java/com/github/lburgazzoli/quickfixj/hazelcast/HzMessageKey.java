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

import com.hazelcast.core.PartitionAware;
import quickfix.SessionID;

import java.io.*;

/**
 * TODO: should we store messages on the same Partition?
 */
public class HzMessageKey implements Externalizable, PartitionAware<String> {
    private int m_sequence;
    private String m_partitionKey;

    /**
     * c-tor
     *
     * @param sequence
     * @param sessionId
     */
    public HzMessageKey(int sequence, SessionID sessionId) {
        m_sequence = sequence;
        m_partitionKey = sessionId.getSessionQualifier();
    }

    public int getSequence() {
        return m_sequence;
    }

    @Override
    public String getPartitionKey() {
        return m_partitionKey;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(m_sequence);
        out.writeUTF(m_partitionKey);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_sequence = in.readInt();
        m_partitionKey = in.readUTF();
    }
}
