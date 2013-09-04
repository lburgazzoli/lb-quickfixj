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

package quickfix.transport.netty.test;

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
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.transport.FIXRuntime;
import quickfix.transport.FIXSession;
import quickfix.transport.netty.FIXSocketInitiator;

/**
 *
 */
public class FIXInitiatorMain {
    private static final Logger LOGGEGR = LoggerFactory.getLogger(FIXInitiatorMain.class);

    /**
     *
     * @param sid
     * @return
     */
    public static SessionSettings getSettingsFor(SessionID sid) {
        SessionSettings cfg  = new SessionSettings();
        cfg.setString(sid,"ConnectionType","initiator");
        cfg.setString(sid,"BeginString","FIX.4.2");
        cfg.setString(sid,"SenderCompID","TEXT");
        cfg.setString(sid,"TargetCompID","EXEC");
        cfg.setString(sid,"ReconnectInterval","30");
        cfg.setString(sid,"HeartBtInt","30");
        cfg.setString(sid,"SocketConnectPort","6543");
        cfg.setString(sid,"SocketConnectHost","localhost");
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

            SessionID             sid  = new SessionID("FIX.4.2","TEST","EXEC");
            SessionSettings       cfg  = getSettingsFor(sid);
            Application           app  = new FIXApplication();
            MessageStoreFactory   msf  = new MemoryStoreFactory();
            LogFactory            logf = new SLF4JLogFactory(cfg);
            MessageFactory        msgf = new DefaultMessageFactory();
            DefaultSessionFactory dsf  = new DefaultSessionFactory(app,msf,logf,msgf);
            FIXRuntime            rt   = new FIXRuntime();

            new FIXSocketInitiator(
                rt,
                new FIXSession(rt,dsf.create(sid,cfg)),
                cfg.getString(sid,"SocketConnectHost"),
                cfg.getInt(sid,"SocketConnectPort")).run();

        } catch(Exception e) {
            LOGGEGR.warn("Exception",e);
        }
    }
}
