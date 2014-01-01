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

import com.github.lburgazzoli.quickfixj.transport.FIXCodecHelper;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;

import java.util.regex.Matcher;

/**
 *
 */
public class ReactorFrameDecoder implements Function<Buffer,byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorFrameDecoder.class);

    private final Consumer<byte[]> m_next;

    /**
     * c-tor
     */
    public ReactorFrameDecoder(final Consumer<byte[]> next) {
        m_next = next;
    }

    // *************************************************************************
    //
    // *************************************************************************


    @Override
    public byte[] apply(Buffer buffer) {
        StopWatch sw = new StopWatch();
        sw.start();

        byte[]  rv    = null;
        boolean reset = true;

        if(buffer.remaining() >= FIXCodecHelper.MSG_MIN_BYTES) {
            // keep track of the initial state fo the buffer
            int pos  = buffer.position();
            int lim  = buffer.limit();

            String  bs = buffer.asString();
            Matcher mh = FIXCodecHelper.getCodecMatcher(bs);

            // reset the buffer
            buffer.position(pos);
            buffer.limit(lim);

            // Extract the FIX message using:
            // -  8 BeginString
            // -  9 BodyLength
            // - 10 CheckSum
            if(mh.find() && (mh.groupCount() == 4)) {
                int offset = mh.start(0);
                int size   = mh.end(4) - mh.start(0) + 1;

                rv    = buffer.createView(offset,offset+size).get().asBytes();
                reset = false;

                buffer.position(mh.end(4));
                buffer.limit(lim);
            }

            if(reset) {
                buffer.position(pos);
                buffer.limit(lim);
            }
        }

        sw.stop();

        LOGGER.debug("Decode.Time <{}><{}>",sw.toString(),sw.getNanoTime());

        return next(rv);
    }

    // **********************************************************************
    //
    // **********************************************************************

    /**
     *
     * @param buffer
     * @return
     */
    private byte[] next(byte[] buffer) {
        if(null != m_next) {
            m_next.accept(buffer);
            return null;
        } else {
            return buffer;
        }
    }
}
