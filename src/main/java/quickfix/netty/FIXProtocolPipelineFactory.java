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

/**
 *
 */
public class FIXProtocolPipelineFactory implements ChannelPipelineFactory {

    private final FIXRuntime m_runtime;

    /**
     * c-tor
     *
     * @param runtime
     */
    public FIXProtocolPipelineFactory(FIXRuntime runtime) {
        m_runtime = runtime;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder",new FIXMessageDecoder(m_runtime));
        pipeline.addLast("encoder",new FIXMessageEncoder(m_runtime));
        pipeline.addLast("handler",new FIXChannelHandler());

        return pipeline;
    }
}
