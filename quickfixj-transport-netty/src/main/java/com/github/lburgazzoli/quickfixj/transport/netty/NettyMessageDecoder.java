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

package com.github.lburgazzoli.quickfixj.transport.netty;

import com.github.lburgazzoli.quickfixj.transport.FIXMessageEvent;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.InvalidMessage;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.MsgType;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class NettyMessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(NettyMessageDecoder.class);


    private static final String REX =
            "(8=FIX[T]?\\.[0-9]\\.[0-9])\\x01(9=[0-9]+)\\x01([0-9].*=*).*\\x01(10=[0-9]+)\\x01";

    private static final int    MSG_MIN_BYTES = 30;
    private static final char   CHAR_EQUALS   = '=';
    private static final char   CHAR_SOH      = 0x01;

    private final Charset m_charset;
    private final FIXSessionHelper m_helper;

    /**
     * c-tor
     *
     * @param runtime
     */
    public NettyMessageDecoder(FIXSessionHelper runtime) {
        this(runtime, CharsetUtil.ISO_8859_1);
    }

    /**
     * c-tor
     *
     * @param helper
     * @param charset
     */
    public NettyMessageDecoder(FIXSessionHelper helper, String charset) {
        this(helper,Charset.forName(charset));
    }

    /**
     * c-tor
     *
     * @param helper
     * @param charset
     */
    public NettyMessageDecoder(FIXSessionHelper helper, Charset charset) {
        m_helper  = helper;
        m_charset = charset;
    }

    // *************************************************************************
    //
    // *************************************************************************

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
        if (in.readableBytes() >= MSG_MIN_BYTES) {
            StopWatch sw = new StopWatch();
            sw.start();

            Object data = doDecodeBuffer(in);

            sw.stop();

            LOGGER.debug("Decode.Time <{}><{}>",sw.toString(),sw.getNanoTime());

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
        String  bs     = in.toString(m_charset);
        Matcher mh     = getMatcherFor(REX,bs);
        boolean reset  = true;

        in.readerIndex(rindex);

        if(mh.find() && (mh.groupCount() == 4)) {
            int offset = mh.start(0);
            int size   = mh.end(4) - mh.start(0) + 1;

            rv    = new byte[size];
            reset = false;

            in.readBytes(rv,offset,size);
            in.readerIndex(mh.end(4)+1);
        }

        if(reset) {
            in.readerIndex(rindex);
        }

        return rv;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param pattern
     * @param data
     * @return
     */
    private Matcher getMatcherFor(String pattern, CharSequence data) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(data);

        return m;
    }

    /**
     *
     * @param data
     * @param dataLen
     * @return
     */
    private int calculateCheckSum(byte[] data, int dataLen) {
        int sum = 0;
        for (int i = 0; i < dataLen; i++) {
            sum += data[i];
        }

        return sum % 256;
    }
}
