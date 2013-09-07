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

import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 *
 */
public abstract class AbstractFIXCommand extends OsgiCommandSupport {
    /**
     *
     */
    protected AbstractFIXCommand() {
    }

    @Override
    protected Object doExecute() throws Exception {
        try {
            execute();
        } finally {
            ungetServices();
        }

        return null;
    }

    protected abstract void execute() throws Exception;
}