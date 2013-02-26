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

package quickfix.test;

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
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.netty.FIXRuntime;
import quickfix.netty.FIXSocketInitiator;

/**
 *
 */
public class FIXInitiatorMain {
    private static final Logger LOGGEGR =
        LoggerFactory.getLogger(FIXInitiatorMain.class);

    public static void main(String[] args) {
        try {
            Application           app  = new FIXApplication();
            SessionSettings       cfg  = new SessionSettings("etc/initiator.cfg");
            MessageStoreFactory   msf  = new MemoryStoreFactory();
            LogFactory            logf = new SLF4JLogFactory(cfg);
            MessageFactory        msgf = new DefaultMessageFactory();
            DefaultSessionFactory dsf  = new DefaultSessionFactory(app,msf,logf,msgf);
            SessionID             sid  = new SessionID("FIX.4.2","BANZAI","EXEC");
            Session               sx   = dsf.create(sid,cfg);

            FIXRuntime         rt = new FIXRuntime();
            FIXSocketInitiator si = new FIXSocketInitiator(rt,sx,"localhost",9878);

            si.run();
        } catch(Exception e) {
            LOGGEGR.warn("Exception",e);
        }
    }
}
