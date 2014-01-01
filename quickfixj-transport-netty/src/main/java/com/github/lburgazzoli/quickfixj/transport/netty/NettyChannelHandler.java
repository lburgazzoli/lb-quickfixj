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
