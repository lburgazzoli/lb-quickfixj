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

/**
 *
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {

    private final FIXSessionHelper m_helper;
    private int m_msgLength;

    /**
     * c-tor
     *
     * @param helper
     */
    public NettyMessageDecoder(FIXSessionHelper helper) {
        m_helper    = helper;
        m_msgLength = -1;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(m_msgLength == -1) {
            if(in.readableBytes() >= 30) {
                int bsi = in.indexOf(0, 12,FIXCodecHelper.BYTE_SOH);
                int bli = in.indexOf(12,20,FIXCodecHelper.BYTE_SOH);

                if( in.getByte(0)       == FIXCodecHelper.BYTE_BEGIN_STRING &&
                    in.getByte(bsi + 1) == FIXCodecHelper.BYTE_BODY_LENGTH  ) {

                    int bl = 0;
                    for(int i=bsi+3;i<bli;i++) {
                        bl *= 10;
                        bl += ((int)in.getByte(i) - (int)'0');
                    }

                    m_msgLength = 1 + bl + bli + FIXCodecHelper.MSG_CSUM_LEN;
                } else {
                    throw new Error("UNexpected state");
                }
            }
        }

        if(m_msgLength != -1 && in.readableBytes() >= m_msgLength){
            if(in.readableBytes() >= m_msgLength) {
                byte[] rv = new byte[m_msgLength];
                in.readBytes(rv);
                out.add(rv);

                m_msgLength = -1;
            } else {
                throw new Error("UNexpected state");
            }
        }
    }
}
