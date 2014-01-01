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
package com.github.lburgazzoli.quickfixj.transport.netty.codec;

import com.github.lburgazzoli.quickfixj.transport.FIXCodecHelper;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.regex.Matcher;

/**
 *
 */
public final class NettyRegExMessageDecoder extends ByteToMessageDecoder {

    private final FIXSessionHelper m_helper;

    /**
     * c-tor
     *
     * @param helper
     */
    public NettyRegExMessageDecoder(FIXSessionHelper helper) {
        m_helper  = helper;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param ctx
     * @param in
     * @param out
     * @return
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= FIXCodecHelper.MSG_MIN_BYTES) {
            Object data = doDecodeBuffer(in);
            if(data != null) {
                out.add(data);
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param in
     * @return
     * @throws Exception
     */
    private Object doDecodeBuffer(ByteBuf in) throws Exception {
        byte[]  rv     = null;
        int     rindex = in.readerIndex();
        String  bs     = in.toString();
        Matcher mh     = FIXCodecHelper.getCodecMatcher(bs);
        boolean reset  = true;

        in.readerIndex(rindex);

        if(mh.find() && (mh.groupCount() == 4)) {
            int offset = mh.start(0);
            int size   = mh.end(4) - mh.start(0) + 1;

            rv    = new byte[size];
            reset = false;

            in.readBytes(rv, offset, size);
            in.readerIndex(mh.end(4) + 1);
        }

        if(reset) {
            in.readerIndex(rindex);
        }

        return rv;
    }
}
