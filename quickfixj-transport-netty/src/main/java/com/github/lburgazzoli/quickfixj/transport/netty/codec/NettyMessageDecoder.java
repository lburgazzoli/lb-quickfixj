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
