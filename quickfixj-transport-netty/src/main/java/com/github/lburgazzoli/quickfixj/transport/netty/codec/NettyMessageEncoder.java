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

import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 *
 */
public final class NettyMessageEncoder extends MessageToMessageEncoder<byte[]> {

    private final FIXSessionHelper m_helper;

    /**
     * c-tor
     *
     * @param helper
     */
    public NettyMessageEncoder(FIXSessionHelper helper) {
        m_helper  = helper;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param ctx
     * @param msg
     * @param out
     * @return
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        if (msg.length != 0) {
            out.add(Unpooled.copiedBuffer(msg));
        }
    }
}
