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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import quickfix.transport.FIXSessionHelper;
import quickfix.transport.FIXSessionType;

/**
 *
 */
public class NettyChannelInitializer extends ChannelInitializer {

    private final FIXSessionHelper m_session;
    private final FIXSessionType m_sessionType;

    /**
     * c-tor
     *
     * @param session
     * @param sessionType
     */
    public NettyChannelInitializer(FIXSessionHelper session, FIXSessionType sessionType) {
        m_session = session;
        m_sessionType = sessionType;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("decoder",new NettyMessageDecoder(m_session.getContext()));
        ch.pipeline().addLast("encoder",new NettyMessageEncoder(m_session.getContext()));
        ch.pipeline().addLast("handler",new NettyChannelHandler(m_session,m_sessionType));
    }
}
