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
     *
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
