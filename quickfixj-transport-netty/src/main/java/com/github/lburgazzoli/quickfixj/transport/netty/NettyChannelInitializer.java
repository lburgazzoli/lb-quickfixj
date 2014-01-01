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
import com.github.lburgazzoli.quickfixj.transport.netty.codec.NettyMessageDecoder;
import com.github.lburgazzoli.quickfixj.transport.netty.codec.NettyMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 *
 */
public class NettyChannelInitializer extends ChannelInitializer {

    private final INettyStateHandler m_stateHandler;
    private final FIXSessionHelper m_helper;
    private final FIXSessionType m_sessionType;

    /**
     * c-tor
     *
     * @param stateHandler
     * @param helper
     * @param sessionType
     */
    public NettyChannelInitializer(INettyStateHandler stateHandler,FIXSessionHelper helper, FIXSessionType sessionType) {
        m_stateHandler = stateHandler;
        m_helper       = helper;
        m_sessionType  = sessionType;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder",new NettyMessageDecoder(m_helper));
        ch.pipeline().addLast("encoder",new NettyMessageEncoder(m_helper));
        ch.pipeline().addLast("handler",new NettyChannelHandler(m_stateHandler,m_helper,m_sessionType));
    }
}
