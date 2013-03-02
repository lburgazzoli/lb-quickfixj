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

import com.google.common.collect.Maps;

import java.util.Map;

/**
 *
 */
public class ChannelAttachment {

    public static final String SESSION_TYPE = "session.type";
    public static final String SESSION      = "session";

    private Map<String,Object> m_data;

    /**
     * c-tor
     */
    public ChannelAttachment() {
        m_data = Maps.newConcurrentMap();
    }

    /**
     * c-tor
     *
     * @param data
     */
    public ChannelAttachment(Map<String, Object> data) {
        m_data = Maps.newConcurrentMap();
        m_data.putAll(data);
    }

    /**
     * @param key
     * @return
     */
    public <T> T get(String key) {
        return (T)m_data.get(key);
    }

    /**
     * @param key
     * @param val
     */
    public void put(String key,Object val) {
        m_data.put(key,val);
    }
}
