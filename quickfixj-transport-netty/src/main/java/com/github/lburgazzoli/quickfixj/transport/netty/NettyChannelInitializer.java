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
