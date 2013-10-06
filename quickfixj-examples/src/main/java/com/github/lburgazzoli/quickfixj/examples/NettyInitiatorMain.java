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

package com.github.lburgazzoli.quickfixj.examples;

import com.github.lburgazzoli.quickfixj.transport.ITransport;
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
import com.github.lburgazzoli.quickfixj.core.FIXContext;
import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import com.github.lburgazzoli.quickfixj.core.util.TracingApplication;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.netty.NettySocketInitiator;

/**
 *
 */
public class NettyInitiatorMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyInitiatorMain.class);

    /**
     * @param sid
     * @return
     */
    public static SessionSettings getSettingsFor(SessionID sid) {
        SessionSettings cfg  = new SessionSettings();
        cfg.setString(sid,"ConnectionType","initiator");
        cfg.setString(sid,"BeginString",sid.getBeginString());
        cfg.setString(sid,"SenderCompID",sid.getSenderCompID());
        cfg.setString(sid,"TargetCompID",sid.getTargetCompID());
        cfg.setString(sid,"ReconnectInterval","30");
        cfg.setString(sid,"HeartBtInt","30");
        cfg.setString(sid,"SocketConnectPort","7001");
        cfg.setString(sid,"SocketConnectHost","exchange.marketcetera.com");
        cfg.setString(sid,"StartTime","00:00:00");
        cfg.setString(sid,"EndTime","23:59:00");
        cfg.setString(sid,"MillisecondsInTimeStamp","Y");
        cfg.setString(sid,"UseDataDictionary","N");
        cfg.setString(sid,"ResetOnLogon","Y");

        return cfg;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            IFIXContext           ctx  = new FIXContext("qfj-ctx-id");
            SessionID             sid  = new SessionID("FIX.4.2","TEST","EXEC");
            SessionSettings       cfg  = getSettingsFor(sid);
            Application           app  = new TracingApplication();
            MessageStoreFactory   msf  = new MemoryStoreFactory(ctx);
            LogFactory            logf = new SLF4JLogFactory(cfg);
            MessageFactory        msgf = new DefaultMessageFactory();
            SessionFactory        sf   = new DefaultSessionFactory(ctx,app,msf,logf,msgf);
            FIXSessionHelper      sx   = new FIXSessionHelper(sf.create(sid,cfg),cfg);
            ITransport            tx   = new NettySocketInitiator(sx);

            tx.connect();

            try {
                while(true) {
                    try{ Thread.sleep(5000); } catch(Exception e) {}
                }
            } catch(Exception e) {
                LOGGER.warn("Exception", e);
            }

        } catch(Exception e) {
            LOGGER.warn("Exception", e);
        }
    }
}
