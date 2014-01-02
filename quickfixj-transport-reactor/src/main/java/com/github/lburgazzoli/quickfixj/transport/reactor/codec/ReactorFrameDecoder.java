/*
 * Copyright 2014 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.quickfixj.transport.reactor.codec;

import com.github.lburgazzoli.quickfixj.transport.FIXCodecHelper;
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
