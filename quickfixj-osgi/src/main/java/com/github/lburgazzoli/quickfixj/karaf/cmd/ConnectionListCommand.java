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

import com.github.lburgazzoli.karaf.common.cmd.AbstractCommand;
import com.github.lburgazzoli.karaf.common.cmd.CommandShellTable;
import com.github.lburgazzoli.quickfixj.osgi.IFIXConnection;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.commands.Argument;

import java.util.List;

/**
 *
 */
@Command(
    scope = "fix",
    name  = "connection-list")
public class ConnectionListCommand extends AbstractCommand {

    private static final String[] COLUMNS = new String[] {
        "Context",
        "BeginString",
        "SenderCompID",
        "TargetCompID",
        "LoggeedOn",
        "RemoteAddress"
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
        CommandShellTable    table  = new CommandShellTable(COLUMNS);
        List<IFIXConnection> ctxs   = getAllServices(IFIXConnection.class,null);

        if(ctxs != null) {
            for(IFIXConnection connection : ctxs) {
                FIXSessionHelper helper = connection.getHelper();
                boolean          add    = true;

                if(StringUtils.isNotEmpty(ctx)) {
                    add = StringUtils.equalsIgnoreCase(ctx,helper.getContext().getId());
                }

                if(add) {
                    table.row(
                        helper.getContext().getId(),
                        helper.getSession().getSessionID().getBeginString(),
                        helper.getSession().getSessionID().getSenderCompID(),
                        helper.getSession().getSessionID().getTargetCompID(),
                        helper.getSession().isLoggedOn(),
                        connection.getRemoteIpAddress()
                    );
                }
            }
        }

        table.print();
    }
}
