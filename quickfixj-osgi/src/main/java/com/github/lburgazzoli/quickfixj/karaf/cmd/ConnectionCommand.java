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

import com.github.lburgazzoli.quickfixj.osgi.IFIXConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

import java.util.List;

/**
 *
 */
@Command(
    scope = "fix",
    name  = "connection")
public class ConnectionCommand extends AbstractFIXCommand {

    // *************************************************************************
    //
    // *************************************************************************

    @Argument(
        index       = 0,
        name        = "id",
        description = "The Connection ID",
        required    = true,
        multiValued = false)
    String id = null;

    @Argument(
        index       = 0,
        name        = "action",
        description = "The Action",
        required    = true,
        multiValued = false)
    String action = null;

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected void execute() throws Exception {
        List<IFIXConnection> ctxs   = getAllServices(IFIXConnection.class,null);

        if(ctxs != null) {
            for(IFIXConnection connection : ctxs) {
                if(StringUtils.equalsIgnoreCase(id,connection.getId())) {
                    if(StringUtils.equalsIgnoreCase(action,"start")) {
                        connection.start();
                    } else if(StringUtils.equalsIgnoreCase(action,"stop")) {
                        connection.stop();
                    }
                }
            }
        }
    }
}
