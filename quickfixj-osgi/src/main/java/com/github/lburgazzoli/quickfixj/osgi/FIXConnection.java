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

import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import com.github.lburgazzoli.quickfixj.core.util.TracingApplication;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.ITransport;
import com.github.lburgazzoli.quickfixj.transport.netty.NettySocketInitiator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Application;
import quickfix.DefaultSessionFactory;
import quickfix.MemoryStoreFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SessionSettingsBuilder;

/**
 *
 */
public class FIXConnection implements IFIXConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(FIXConnection.class);

    private final IFIXContext m_fixCtx;
    private final SessionSettingsBuilder m_settingsBuilder;
    private final String m_cfgId;
    private SessionID m_sessionId;
    private ITransport m_cnx;

    /**
     *
     * @param fixCtx
     * @param cfgId
     * @param settinBuilder
     */
    public FIXConnection(final IFIXContext fixCtx, String cfgId, final SessionSettingsBuilder settinBuilder) {
        m_fixCtx          = fixCtx;
        m_settingsBuilder = settinBuilder;
        m_sessionId       = null;
        m_cnx             = null;
        m_cfgId           = cfgId;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        SessionSettings cfg = m_settingsBuilder.build();
        try {
            m_sessionId = createSessionID(cfg);
            if(isInitiator(cfg)) {
                m_cnx = initInitator(cfg);
            } else if(isAcceptor(cfg)) {
                m_cnx = initAcceptor(cfg);
            } else {
                //TODO: error
            }
        } catch (Exception e) {
            LOGGER.warn("Exception",e);
        }
    }

    @Override
    public void destroy() {
        stop();

        m_cnx = null;
    }

    @Override
    public void start() {
        if(m_cnx != null) {
            m_cnx.connect();
        }
    }

    @Override
    public void stop() {
        if(m_cnx != null) {
            m_cnx.disconnect();
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public String getId() {
        return m_cfgId;
    }

    @Override
    public FIXSessionHelper getHelper() {
        return m_cnx != null ? m_cnx.getHelper() : null;
    }

    @Override
    public String getRemoteIpAddress() {
        return m_cnx != null ? m_cnx.getRemoteIPAddress() : null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param settings
     * @return
     */
    private SessionID createSessionID(final SessionSettings settings) throws Exception {
        return new SessionID(
            settings.getString(SessionSettings.BEGINSTRING),
            settings.getString(SessionSettings.SENDERCOMPID),
            settings.getString(SessionSettings.SENDERSUBID),
            settings.getString(SessionSettings.SENDERLOCID),
            settings.getString(SessionSettings.TARGETCOMPID),
            settings.getString(SessionSettings.TARGETSUBID),
            settings.getString(SessionSettings.TARGETLOCID),
            settings.getString(SessionSettings.SESSION_QUALIFIER)
        );
    }

    /**
     *
     * @param settings
     * @return
     */
    private boolean isInitiator(final SessionSettings settings) throws Exception {
        return StringUtils.equalsIgnoreCase(
            SessionFactory.INITIATOR_CONNECTION_TYPE,
            settings.getString(SessionFactory.SETTING_CONNECTION_TYPE)
        );
    }

    /**
     *
     * @param settings
     * @return
     */
    private boolean isAcceptor(final SessionSettings settings) throws Exception {
        return StringUtils.equalsIgnoreCase(
            SessionFactory.ACCEPTOR_CONNECTION_TYPE,
            settings.getString(SessionFactory.SETTING_CONNECTION_TYPE)
        );
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param settings
     */
    private ITransport initInitator(final SessionSettings settings) throws Exception {
        Application         app  = new TracingApplication();
        MessageStoreFactory msf  = new MemoryStoreFactory(m_fixCtx);
        SessionFactory      sf   = new DefaultSessionFactory(m_fixCtx,settings,app,msf);
        FIXSessionHelper    sx   = new FIXSessionHelper(sf.create(m_sessionId,settings),settings);

        return new NettySocketInitiator(sx);
    }

    /**
     *
     * @param settings
     */
    private ITransport initAcceptor(final SessionSettings settings) throws Exception {
        return null;
    }
}
