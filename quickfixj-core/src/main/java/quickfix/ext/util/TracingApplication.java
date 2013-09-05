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

package quickfix.ext.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

/**
 *
 */
public class TracingApplication implements Application {
    private static final Logger LOGEGR =
        LoggerFactory.getLogger(TracingApplication.class);

    @Override
    public void onCreate(SessionID sessionID) {
        LOGEGR.debug("onCreate {}",sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        LOGEGR.debug("onLogon {}",sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        LOGEGR.debug("onLogout {}",sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        LOGEGR.debug("toAdmin {} => {}",sessionID,message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        LOGEGR.debug("fromAdmin {} => {}",sessionID,message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID)
        throws DoNotSend {
        LOGEGR.debug("toApp {} => {}",sessionID,message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        LOGEGR.debug("fromApp {} => {}",sessionID,message);
    }
}
