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

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;
import reactor.tcp.encoding.Codec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ReactorFrameCodec implements Codec<Buffer,byte[],byte[]> {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ReactorFrameCodec.class);

    private static final String REX =
        "(8=FIX[T]?\\.[0-9]\\.[0-9])\\x01(9=[0-9]+)\\x01([0-9].*=*).*\\x01(10=[0-9]+)\\x01";

    private static final int  MSG_MIN_CHARS = 30;
    private static final char CHAR_EQUALS   = '=';
    private static final char CHAR_SOH      = 0x01;

    /**
     * c-tor
     */
    public ReactorFrameCodec() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public Function<Buffer, byte[]> decoder(final Consumer<byte[]> next) {
        return new Decoder(next);
    }

    @Override
    public Function<byte[], Buffer> encoder() {
        return new Encoder();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * Decoder
     */
    private final class Decoder implements Function<Buffer,byte[]> {
        private final Consumer<byte[]> m_next;

        /**
         *
         * @param next
         */
        public Decoder(final Consumer<byte[]> next) {
            m_next = next;
        }

        @Override
        public byte[] apply(Buffer buffer) {
            StopWatch sw = new StopWatch();
            sw.start();

            byte[]  rv    = null;
            boolean reset = true;

            if(buffer.remaining() >= MSG_MIN_CHARS) {
                // keep track of the initial state fo the buffer
                int pos  = buffer.position();
                int lim  = buffer.limit();

                String  bs = buffer.asString();
                Matcher mh = getMatcherFor(REX,bs);

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

        /**
         *
         * @param pattern
         * @param data
         * @return
         */
        private Matcher getMatcherFor(String pattern, CharSequence data) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(data);

            return m;
        }
    }

    /**
     * Encoder
     */
    private final class Encoder implements Function<byte[], Buffer> {
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
}
