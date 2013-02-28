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

package quickfix.netty.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.InvalidMessage;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.netty.FIXMessageEvent;
import quickfix.netty.FIXRuntime;

import java.nio.charset.Charset;

/**
 *
 */
public class FIXMessageDecoder extends FrameDecoder {

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
        this(runtime,"US-ASCII");
    }

    /**
     * c-tor
     *
     * @param charset
     * @param runtime
     */
    public FIXMessageDecoder(FIXRuntime runtime,String charset) {
        m_runtime = runtime;
        m_charset = Charset.forName(charset);
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
     * @param channel
     * @param buffer
     * @return
     * @throws Exception
     */
    @Override
    protected Object decode(
        ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < 13) {
            // The length field was not received yet - return null.
            return null;
        }

        int    endOfLen = buffer.indexOf(12,buffer.readableBytes(),(byte)'\01');
        String lenStr   = buffer.slice(12, endOfLen-12).toString(m_charset);
        int    len      = Integer.parseInt(lenStr);
        int    totalLen = len + endOfLen + 8;

        if (buffer.readableBytes() >= totalLen) {
            return processMessageString(channel,buffer.readBytes(totalLen));
        }

        return null;
    }

    /**
     *
     * @param channel
     * @param buffer
     * @return
     * @throws Exception
     */
    private Object processMessageString(Channel channel,ChannelBuffer buffer) throws Exception {
        String    message   = buffer.toString(m_charset);
        SessionID sessionid = MessageUtils.getReverseSessionID(message);
        Session   session   = m_runtime.find(sessionid);

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
            LOGGER.error("Disconnecting; received message for unknown session: " + message);
            channel.close();
        }

        return null;
    }
}
