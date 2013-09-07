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

import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class NettyChannelHandler extends SimpleChannelInboundHandler<byte[]> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(NettyChannelHandler.class);

    private final INettyStateHandler m_stateHandler;
    private final FIXSessionType m_sessionType;
    private final FIXSessionHelper m_helper;

    private static final AttributeKey<FIXSessionType> ATTR_SESSION_TYPE =
        new AttributeKey<FIXSessionType>("Session.Type");

    /**
     * c-tor
     *
     * @param stateHandler
     * @param helper
     * @param sessionType
     */
    public NettyChannelHandler(INettyStateHandler stateHandler,FIXSessionHelper helper, FIXSessionType sessionType) {
        m_stateHandler = stateHandler;
        m_helper       = helper;
        m_sessionType  = sessionType;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        ctx.attr(ATTR_SESSION_TYPE).set(m_sessionType);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(m_sessionType == FIXSessionType.INITIATOR) {
            m_helper.getSession().logon();
            m_helper.startSessionTimer();
        }

        m_stateHandler.onConnect(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,byte[] msg) throws Exception {
        m_helper.processIncomingRawMessage(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        m_helper.stopSessionTimer();
        m_stateHandler.onDisconnect(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
