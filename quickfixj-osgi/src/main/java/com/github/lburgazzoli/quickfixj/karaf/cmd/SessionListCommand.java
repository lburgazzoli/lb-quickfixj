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

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import quickfix.Session;
import quickfix.SessionID;
import com.github.lburgazzoli.quickfixj.core.IFIXContext;

import java.util.List;

/**
 *
 */
@Command(
    scope = "fix",
    name  = "session-list")
public class SessionListCommand extends AbstractFIXCommand {

    private static final String[] COLUMNS = new String[] {
        "Context",
        "BeginString",
        "SenderCompID",
        "TargetCompID",
        "LoggeedOn",
        "ExpectedSenderNum",
        "ExpectedTargetNum"
    };

    // *************************************************************************
    //
    // *************************************************************************

    @Argument(
        index       = 0,
        name        = "ctx",
        description = "The Context ID",
        required    = false,
        multiValued = false)
    String ctx = null;

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected void execute() throws Exception {
        ShellTable        table  = new ShellTable(COLUMNS);
        List<IFIXContext> ctxs   = getAllServices(IFIXContext.class,null);

        if(ctxs != null) {
            for(IFIXContext context : ctxs) {
                for(SessionID sid : context.getSessionIDs()) {
                    Session session = context.getSession(sid);
                    boolean add     = true;

                    if(StringUtils.isNotEmpty(ctx)) {
                        add = StringUtils.equalsIgnoreCase(ctx,context.getId());
                    }

                    table.addRow(
                        context.getId(),
                        sid.getBeginString(),
                        sid.getSenderCompID(),
                        sid.getTargetCompID(),
                        session.isLoggedOn(),
                        session.getExpectedSenderNum(),
                        session.getExpectedTargetNum()
                    );
                }
            }
        }

        table.print();
    }
}