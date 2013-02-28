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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import quickfix.Session;
import quickfix.netty.codec.FIXChannelHandler;
import quickfix.netty.codec.FIXMessageDecoder;
import quickfix.netty.codec.FIXMessageEncoder;

/**
 *
 */
public class FIXProtocolPipelineFactory implements ChannelPipelineFactory {

    private final FIXSession m_session;
    private final FIXSessionType m_sessionType;

    /**
     * c-tor
     *
     * @param runtime
     */
    public FIXProtocolPipelineFactory(
        FIXSession session,FIXSessionType sessionType) {
        m_session = session;
        m_sessionType = sessionType;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder",new FIXMessageDecoder(m_session.getRuntime()));
        pipeline.addLast("encoder",new FIXMessageEncoder(m_session.getRuntime()));
        pipeline.addLast("handler",new FIXChannelHandler(m_session,m_sessionType));

        return pipeline;
    }
}
