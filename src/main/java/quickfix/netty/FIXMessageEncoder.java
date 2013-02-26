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

package quickfix.netty;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.netty.util.CharsetUtil;
import quickfix.Message;

import java.nio.charset.Charset;

/**
 *
 */
public class FIXMessageEncoder extends OneToOneEncoder {
    private final Charset m_charset;
    private final FIXRuntime m_runtime;

    /**
     * c-tor
     *
     * @param runtime
     */
    public FIXMessageEncoder(FIXRuntime runtime) {
        this(runtime, CharsetUtil.US_ASCII);
    }

    /**
     * c-tor
     *
     * @param charset
     * @param runtime
     */
    public FIXMessageEncoder(FIXRuntime runtime,Charset charset) {
        m_runtime = runtime;
        m_charset = charset;
    }

    /**
     *
     * @param ctx
     * @param channel
     * @param msg
     * @return
     * @throws Exception
     */
    @Override
    protected Object encode(
        ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        String message = null;
        if(msg instanceof Message) {
            message = ((Message)msg).toString();
        } else if(msg instanceof String) {
            message = (String)msg;
        } else if(msg instanceof FIXMessageEvent) {
            message = ((FIXMessageEvent)msg).getMessage().toString();
        }

        if(StringUtils.isNotBlank(message)) {
            return ChannelBuffers.copiedBuffer(
                ctx.getChannel().getConfig().getBufferFactory().getDefaultOrder(),
                message,
                m_charset);
        }

        return msg;
    }
}
