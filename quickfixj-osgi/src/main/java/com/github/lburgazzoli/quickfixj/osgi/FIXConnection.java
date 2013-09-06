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

import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.ITransport;
import com.github.lburgazzoli.quickfixj.transport.netty.NettySocketInitiator;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DefaultSessionFactory;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ext.IFIXContext;
import quickfix.ext.util.TracingApplication;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 *
 * <reference
 *     id        = "configurationAdmin"
 *     interface = "org.osgi.service.cm.ConfigurationAdmin"/>
 */
public class FIXConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(FIXConnection.class);

    private final IFIXContext        m_fixCtx;
    private final BundleContext      m_bdlCtx;
    private final ConfigurationAdmin m_cfgAdm;
    private final String             m_cfgId;
    private ITransport               m_cnx;

    /**
     *
     * @param fixCtx
     * @param cfgId
     * @param bdlCtx
     * @param cfgAdm
     */
    public FIXConnection(IFIXContext fixCtx, String cfgId, BundleContext bdlCtx, ConfigurationAdmin cfgAdm) {
        m_fixCtx  = fixCtx;
        m_bdlCtx  = bdlCtx;
        m_cfgAdm  = cfgAdm;
        m_cfgId   = cfgId;
        m_cnx     = null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public void init() {
        try {
            Configuration             cfg  = m_cfgAdm.getConfiguration(m_cfgId);
            Dictionary<String,Object> prop = cfg.getProperties();
            SessionID                 sid  = buildSessionID(prop);

            if(sid != null) {
                SessionSettings     settings = new SessionSettings();
                Enumeration<String> keys     = prop.keys();
                String              key      = null;

                while(keys.hasMoreElements()) {
                    key = keys.nextElement();
                    settings.setString(sid,key,(String)prop.get(key));
                }

                try {
                    if(isInitiator(prop)) {
                        m_cnx = initInitator(sid,settings);
                    } else if(isAcceptor(prop)) {
                        m_cnx = initAcceptor(sid,settings);
                    }

                    if(m_cnx != null) {
                        m_cnx.run();
                    }
                } catch (Exception e) {
                    LOGGER.warn("Exception",e);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("IOException",e);
        }
    }

    /**
     *
     */
    public void destroy() {
        if(m_cnx != null) {
            m_cnx.disconnect();
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param prop
     * @return
     */
    private SessionID buildSessionID(Dictionary<String,Object> prop) {
        return new SessionID(
            (String)prop.get(SessionSettings.BEGINSTRING),
            (String)prop.get(SessionSettings.SENDERCOMPID),
            (String)prop.get(SessionSettings.SENDERSUBID),
            (String)prop.get(SessionSettings.SENDERLOCID),
            (String)prop.get(SessionSettings.TARGETCOMPID),
            (String)prop.get(SessionSettings.TARGETSUBID),
            (String)prop.get(SessionSettings.TARGETLOCID),
            (String)prop.get(SessionSettings.SESSION_QUALIFIER));
    }

    /**
     *
     * @param prop
     * @return
     */
    private boolean isInitiator(Dictionary<String,Object> prop ) {
        return StringUtils.equalsIgnoreCase(
            SessionFactory.INITIATOR_CONNECTION_TYPE,
            (String)prop.get(SessionFactory.SETTING_CONNECTION_TYPE)
        );
    }

    /**
     *
     * @param prop
     * @return
     */
    private boolean isAcceptor(Dictionary<String,Object> prop ) {
        return StringUtils.equalsIgnoreCase(
            SessionFactory.ACCEPTOR_CONNECTION_TYPE,
            (String)prop.get(SessionFactory.SETTING_CONNECTION_TYPE)
        );
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param settings
     */
    private ITransport initInitator(SessionID sid,SessionSettings settings) throws Exception {
        Application         app  = new TracingApplication();
        MessageStoreFactory msf  = new MemoryStoreFactory(m_fixCtx);
        LogFactory          logf = new SLF4JLogFactory(settings);
        MessageFactory      msgf = new DefaultMessageFactory();
        SessionFactory      sf   = new DefaultSessionFactory(m_fixCtx,app,msf,logf,msgf);
        FIXSessionHelper    sx   = new FIXSessionHelper(sf.create(sid,settings),settings);

        return new NettySocketInitiator(sx);
    }

    /**
     *
     * @param settings
     */
    private ITransport initAcceptor(SessionID sid,SessionSettings settings) throws Exception {
        return null;
    }
}
