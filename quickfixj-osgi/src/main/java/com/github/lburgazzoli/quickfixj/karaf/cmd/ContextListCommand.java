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

import com.github.lburgazzoli.quickfixj.core.IFIXContext;
import org.apache.felix.gogo.commands.Command;

import java.util.List;

/**
 *
 */
@Command(
    scope = "fix",
    name  = "context-list")
public class ContextListCommand extends AbstractFIXCommand {

    private static final String[] COLUMNS = new String[] {
        "Context",
        "NumSessions"
    };

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected void execute() throws Exception {
        ShellTable        table  = new ShellTable(COLUMNS);
        List<IFIXContext> ctxs   = getAllServices(IFIXContext.class,null);

        if(ctxs != null) {
            for(IFIXContext context : ctxs) {
                table.addRow(
                    context.getId(),
                    context.getNumSessions()
                );
            }
        }

        table.print();
    }
}