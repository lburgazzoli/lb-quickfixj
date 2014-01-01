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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import com.github.lburgazzoli.quickfixj.transport.ITransportChannel;

/**
 *
 */
public class NettyChannel implements ITransportChannel {

    private final Channel m_channel;

    /**
     * c-tor
     * @param channel
     */
    public NettyChannel(Channel channel) {
        m_channel = channel;
    }

    /**
     *
     * @param data
     * @return
     */
    @Override
    public boolean send(String data) {
        if(m_channel != null) {
            m_channel.writeAndFlush(data.getBytes());
            return true;
        }

        return false;
    }

    /**
     *
     */
    @Override
    public boolean disconnect() {
        if(m_channel != null) {
            m_channel.disconnect().awaitUninterruptibly(5000L);

            ChannelFuture future = m_channel.close();
            future.awaitUninterruptibly();

            return future.isSuccess();
        }

        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getRemoteIPAddress() {
        return m_channel != null ? m_channel.remoteAddress().toString() : null;
    }
}
