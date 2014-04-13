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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SessionSettingsBuilder;

import java.util.Map;

/**
 *
 */
public class HzSessionSettingsBuilder implements SessionSettingsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(HzSessionSettingsBuilder.class);

    private final Map<String,String> m_data;

    /**
     * c-tor
     *
     * @param instance
     * @param sessionId
     */
    public HzSessionSettingsBuilder(final HazelcastInstance instance, SessionID sessionId) {
        this(instance,sessionId.toString());
    }

    /**
     * c-tor
     *
     * @param instance
     * @param key
     */
    public HzSessionSettingsBuilder(final HazelcastInstance instance, String key) {
        m_data = instance.getMap(key);

    }

    @Override
    public SessionSettings build() {
        SessionSettings settings = new SessionSettings();

        if(validate(m_data)) {
            for(String key : m_data.keySet()) {
                settings.setString(key,m_data.get(key));
            }
        }

        return settings;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param prop
     * @return
     */
    private boolean validate(Map<String,String> prop) {
        return StringUtils.isNoneBlank((String)prop.get(SessionSettings.BEGINSTRING))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.SENDERCOMPID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.SENDERSUBID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.SENDERLOCID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.TARGETCOMPID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.TARGETSUBID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.TARGETLOCID))
            && StringUtils.isNoneBlank((String)prop.get(SessionSettings.SESSION_QUALIFIER))
            && StringUtils.isNoneBlank((String)prop.get(SessionFactory.INITIATOR_CONNECTION_TYPE));
    }
}
