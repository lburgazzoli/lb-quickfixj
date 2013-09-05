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

package quickfix.transport.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import quickfix.transport.FIXRuntime;

import java.nio.charset.Charset;
import java.util.List;

/**
 *
 */
public final class NettyMessageEncoder extends MessageToMessageEncoder<CharSequence> {
    private final Charset m_charset;
    private final FIXRuntime m_runtime;

    /**
     * c-tor
     *
     * @param runtime
     */
    public NettyMessageEncoder(FIXRuntime runtime) {
        this(runtime, CharsetUtil.ISO_8859_1);
    }

    /**
     * c-tor
     *
     * @param charset
     * @param runtime
     */
    public NettyMessageEncoder(FIXRuntime runtime, Charset charset) {
        m_runtime = runtime;
        m_charset = charset;
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
    protected void encode(
        ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() == 0) {
            return;
        }

        out.add(Unpooled.copiedBuffer(msg, m_charset));
    }
}
