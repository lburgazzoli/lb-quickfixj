/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.quickfixj.examples

import com.github.lburgazzoli.quickfixj.core.FIXContext
import com.github.lburgazzoli.quickfixj.core.IFIXContext
import com.github.lburgazzoli.quickfixj.core.util.TracingApplication
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper
import com.github.lburgazzoli.quickfixj.transport.ITransport
import com.github.lburgazzoli.quickfixj.transport.netty.NettySocketInitiator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import quickfix.Application
import quickfix.DefaultSessionFactory
import quickfix.MemoryStoreFactory
import quickfix.MessageStoreFactory
import quickfix.SessionFactory
import quickfix.SessionID
import quickfix.SessionSettings


class InitiatorMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitiatorMain.class);

    /**
     * @param sid
     * @return
     */
    static def SessionSettings getSettingsFor(SessionID sid) {
        SessionSettings cfg  = new SessionSettings();
        cfg.setString("ConnectionType","initiator");
        cfg.setString("BeginString",sid.beginString);
        cfg.setString("SenderCompID",sid.senderCompID);
        cfg.setString("TargetCompID",sid.targetCompID);
        cfg.setString("SessionQualifier",sid.sessionQualifier);
        cfg.setString("ReconnectInterval","30");
        cfg.setString("HeartBtInt","30");
        cfg.setString("SocketConnectPort","7001");
        cfg.setString("SocketConnectHost","exchange.marketcetera.com");
        cfg.setString("StartTime","00:00:00");
        cfg.setString("EndTime","23:59:00");
        cfg.setString("MillisecondsInTimeStamp","Y");
        cfg.setString("UseDataDictionary","N");
        cfg.setString("ResetOnLogon","Y");

        return cfg;
    }

    /**
     * @param args
     */
    static def main(String[] args) {
        try {
            SessionID           sid  = new SessionID("FIX.4.2","TEST","EXEC","FIX.4.2:TEST->EXEC");
            SessionSettings     cfg  = getSettingsFor(sid);
            IFIXContext         ctx  = new FIXContext("qfj-ctx");
            Application         app  = new TracingApplication();
            MessageStoreFactory msf  = new MemoryStoreFactory(ctx);
            SessionFactory      sf   = new DefaultSessionFactory(ctx,cfg,app,msf);
            FIXSessionHelper    sx   = new FIXSessionHelper(sf.create(sid,cfg),cfg);
            ITransport          tx   = new NettySocketInitiator(sx);

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
