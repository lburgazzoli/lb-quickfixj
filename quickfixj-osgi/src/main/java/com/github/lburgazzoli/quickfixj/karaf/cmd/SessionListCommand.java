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

package com.github.lburgazzoli.quickfixj.karaf.cmd;

import org.apache.felix.gogo.commands.Command;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.ext.IFIXContext;

/**
 *
 */
@Command(
    scope = "fix",
    name  = "session-list")
public class SessionListCommand extends AbstractFIXCommand {

    private static final String[] COLUMNS = new String[] {
        "BeginString",
        "SenderCompID",
        "TargetCompID",
        "ExpectedSenderNum",
        "ExpectedTargetNum"
    };

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected void execute() throws Exception {
        ShellTable  table  = new ShellTable(COLUMNS);
        IFIXContext contxt = getFixContext();

        for(SessionID sid : contxt.getSessionIDs()) {
            Session session = contxt.getSession(sid);
            table.addRow(
                sid.getBeginString(),
                sid.getSenderCompID(),
                sid.getTargetCompID(),
                session.getExpectedSenderNum(),
                session.getExpectedTargetNum()
            );
        }

        table.print();
    }
}
