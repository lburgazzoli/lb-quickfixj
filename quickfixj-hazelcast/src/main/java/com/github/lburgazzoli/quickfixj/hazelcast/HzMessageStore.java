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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import org.apache.commons.lang3.StringUtils;
import quickfix.MessageStore;
import quickfix.SessionID;
import quickfix.SystemTime;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class HzMessageStore implements MessageStore {
    private final SessionID m_sid;
    private final HazelcastInstance m_hz;
    private final Map<Integer,String> m_messages;
    private final IAtomicLong m_senderMsgSeqNum;
    private final IAtomicLong m_targetMsgSeqNum;
    private final IAtomicLong m_creationTime;

    /**
     * c-tor
     *
     * @param sessionID
     * @param hz
     */
    public HzMessageStore(final SessionID sessionID, final HazelcastInstance hz) {
        m_sid = sessionID;
        m_hz = hz;

        m_messages        = m_hz.getMap(HzUtils.hzName(m_sid,"messages"));
        m_senderMsgSeqNum = m_hz.getAtomicLong(HzUtils.hzName(m_sid,"senderMsgSeqNum"));
        m_targetMsgSeqNum = m_hz.getAtomicLong(HzUtils.hzName(m_sid,"targetMsgSeqNum"));
        m_creationTime    = m_hz.getAtomicLong(HzUtils.hzName(m_sid,"creationTime"));

        if(m_creationTime.get() <= 0) {
            m_creationTime.set(SystemTime.getUtcCalendar().getTimeInMillis());
        }
    }

    @Override
    public boolean set(int sequence, String message) throws IOException {
        m_messages.put(sequence,message);
        return true;
    }

    @Override
    public void get(int startSequence, int endSequence, Collection<String> messages) throws IOException {
        for(int i=startSequence;i<=endSequence;i++) {
            String message = m_messages.get(i);
            if(StringUtils.isNotBlank(message)) {
                messages.add(message);
            }
        }
    }

    @Override
    public int getNextSenderMsgSeqNum() throws IOException {
        return (int)m_senderMsgSeqNum.get() + 1;
    }

    @Override
    public int getNextTargetMsgSeqNum() throws IOException {
        return (int)m_targetMsgSeqNum.get() + 1;
    }

    @Override
    public void setNextSenderMsgSeqNum(int next) throws IOException {
        m_senderMsgSeqNum.set(next);
    }

    @Override
    public void setNextTargetMsgSeqNum(int next) throws IOException {
        m_targetMsgSeqNum.set(next);
    }

    @Override
    public void incrNextSenderMsgSeqNum() throws IOException {
        m_senderMsgSeqNum.incrementAndGet();
    }

    @Override
    public void incrNextTargetMsgSeqNum() throws IOException {
        m_targetMsgSeqNum.incrementAndGet();
    }

    @Override
    public Date getCreationTime() throws IOException {
        return new Date(m_creationTime.get());
    }

    @Override
    public synchronized void reset() throws IOException {
        setNextSenderMsgSeqNum(1);
        setNextTargetMsgSeqNum(1);
        m_messages.clear();
        m_creationTime.set(SystemTime.getUtcCalendar().getTimeInMillis());
    }

    @Override
    public synchronized void refresh() throws IOException {
    }
}
