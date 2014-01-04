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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {

    private int m_msgLength;

    /**
     * c-tor
     */
    public NettyMessageDecoder() {
        m_msgLength = -1;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(m_msgLength == -1) {
            if(in.readableBytes() >= FIXCodecHelper.MSG_MIN_BYTES) {
                int ridx  = in.readerIndex();
                int bssohidx = in.indexOf(ridx     ,ridx + 12,FIXCodecHelper.BYTE_SOH);
                int blsohidx = in.indexOf(ridx + 12,ridx + 20,FIXCodecHelper.BYTE_SOH);

                // check the existence of:
                // - BeginString 8=
                // - BodyLength  9=
                if( in.getByte(ridx)          == FIXCodecHelper.BYTE_BEGIN_STRING &&
                    in.getByte(ridx + 1)      == FIXCodecHelper.BYTE_EQUALS       &&
                    in.getByte(bssohidx + 1)  == FIXCodecHelper.BYTE_BODY_LENGTH  &&
                    in.getByte(bssohidx + 2)  == FIXCodecHelper.BYTE_EQUALS       ) {

                    int bodyLength = 0;
                    for(int i=bssohidx+3;i<blsohidx;i++) {
                        bodyLength *= 10;
                        bodyLength += ((int)in.getByte(i) - (int)'0');
                    }

                    m_msgLength = 1 + bodyLength + (blsohidx - ridx) + FIXCodecHelper.MSG_CSUM_LEN;
                } else {
                    throw new Error("Unexpected state (header)");
                }
            }
        }

        if(m_msgLength != -1 && in.readableBytes() >= m_msgLength){
            if(in.readableBytes() >= m_msgLength) {
                byte[] rv = new byte[m_msgLength];
                in.readBytes(rv);

                //TODO: validate checksum
                out.add(rv);

                m_msgLength = -1;
            } else {
                throw new Error("Unexpected state (body)");
            }
        }
    }
}
