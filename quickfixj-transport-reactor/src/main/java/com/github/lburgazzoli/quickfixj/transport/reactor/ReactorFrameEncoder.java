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

package com.github.lburgazzoli.quickfixj.transport.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.function.Function;
import reactor.io.Buffer;

/**
 *
 */
public class ReactorFrameEncoder implements Function<byte[], Buffer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorFrameEncoder.class);

    /**
     * c-tor
     */
    public ReactorFrameEncoder() {
    }

    @Override
    public Buffer apply(byte[] buffer) {
        if (null == buffer) {
            return null;
        } else if (buffer.length == 0) {
            return null;
        }

        return Buffer.wrap(buffer);
    }
}
