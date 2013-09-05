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

package quickfix.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.InvalidMessage;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.transport.FIXMessageEvent;
import quickfix.transport.FIXRuntime;

import java.nio.charset.Charset;
import java.util.List;

/**
 *
 */
public class FIXMessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FIXMessageDecoder.class);

    private final Charset m_charset;
    private final FIXRuntime m_runtime;

    /**
     * c-tor
     *
     * @param runtime
     */
    public FIXMessageDecoder(FIXRuntime runtime) {
        this(runtime, CharsetUtil.ISO_8859_1);
    }

    /**
     * c-tor
     *
     * @param charset
     * @param runtime
     */
    public FIXMessageDecoder(FIXRuntime runtime,String charset) {
        this(runtime,Charset.forName(charset));
    }

    /**
     * c-tor
     *
     * @param charset
     * @param runtime
     */
    public FIXMessageDecoder(FIXRuntime runtime,Charset charset) {
        m_runtime = runtime;
        m_charset = charset;
    }

    /**
     *
     * 8=FIX.4.0|9=    => len=12
     * 8=FIX.4.1|9=    => len=12
     * 8=FIX.4.2|9=    => len=12
     * 8=FIX.4.3|9=    => len=12
     * 8=FIX.4.4|9=    => len=12
     * 8=FIXT.1.1|9=   => len=13
     *
     * @param ctx
     * @param in
     * @param out
     * @return
     * @throws Exception
     */
    @Override
    public void decode(
        ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() >= 13) {

            int    endOfLen = in.indexOf(12,in.readableBytes(),(byte)'\01');
            String lenStr   = in.slice(12, endOfLen-12).toString(m_charset);
            int    len      = Integer.parseInt(lenStr);
            int    totalLen = len + endOfLen + 8;

            if (in.readableBytes() >= totalLen) {
                Object msg = processMessageString(ctx,in.readBytes(totalLen));
                if(msg != null) {
                    out.add(msg);
                }
            }
        }
    }

    /**
     *
     * @param channel
     * @param buffer
     * @return
     * @throws Exception
     */
    private Object processMessageString(ChannelHandlerContext channel,ByteBuf buffer) throws Exception {
        String    message   = buffer.toString(m_charset);
        SessionID sessionid = MessageUtils.getReverseSessionID(message);
        Session   session   = m_runtime.getSession(sessionid);

        if (session != null) {
            session.getLog().onIncoming(message);
            try {
                return new FIXMessageEvent(session,MessageUtils.parse(session, message));
            } catch (InvalidMessage e) {
                if (MsgType.LOGON.equals(MessageUtils.getMessageType(message))) {
                    LOGGER.error("Invalid LOGON message, disconnecting: " + e.getMessage());
                    channel.close();
                } else {
                    LOGGER.error("Invalid message: " + e.getMessage());
                }
            }
        } else {
            LOGGER.error("Disconnecting: received message for unknown session: " + message);
            channel.close();
        }

        return null;
    }
}
