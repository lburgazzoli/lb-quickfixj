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


package com.github.lburgazzoli.quickfixj.osgi;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionFactory;
import quickfix.SessionSettings;
import quickfix.SessionSettingsBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 *
 */
public class ConfigAdminSessionSettingsBuilder implements SessionSettingsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigAdminSessionSettingsBuilder.class);

    private final String m_pid;
    private final ConfigurationAdmin m_cfgAdm;

    /**
     * c-tor
     *
     * @param cfgAdm
     * @param pid
     */
    public ConfigAdminSessionSettingsBuilder(final ConfigurationAdmin cfgAdm, String pid) {
        m_cfgAdm = cfgAdm;
        m_pid = pid;
    }

    @Override
    public SessionSettings build() {
        SessionSettings settings = new SessionSettings();

        try {
            Configuration cfg  = m_cfgAdm.getConfiguration(m_pid);
            Dictionary<String,Object> prop = cfg.getProperties();

            if(validate(prop)) {
                Enumeration<String> keys = prop.keys();
                String              key  = null;

                while(keys.hasMoreElements()) {
                    key = keys.nextElement();
                    settings.setString(key,(String)prop.get(key));
                }
            }
        } catch (IOException e) {
            LOGGER.warn("IOException",e);
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
    private boolean validate(Dictionary<String,Object> prop) {
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
