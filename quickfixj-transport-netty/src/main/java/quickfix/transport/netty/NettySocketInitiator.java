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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionID;
import quickfix.transport.AbstractTransport;
import quickfix.transport.FIXSessionHelper;
import quickfix.transport.FIXSessionType;

import java.net.InetSocketAddress;

/**
 *
 */
public class NettySocketInitiator extends AbstractTransport {
    private static Logger LOGGER =
        LoggerFactory.getLogger(NettySocketInitiator.class);

    /**
     * c-tor
     *
     * @param session
     */
    public NettySocketInitiator(FIXSessionHelper session) {
        super(session);
    }

    /**
     *
     */
    public void stop() {
        setRunning(false);
        disconnect();
    }

    /**
     *
     */
    @Override
    public void run() {
        Bootstrap b = null;

        try {
            b = new Bootstrap();
            b.group(new NioEventLoopGroup());
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new NettyChannelInitializer(getSession(), FIXSessionType.INITIATOR));

            SessionID sid  = getSession().getSession().getSessionID();
            String    host = getSession().getSettings().getString(sid,"SocketConnectHost");
            int       port = getSession().getSettings().getInt(sid, "SocketConnectPort");

            ChannelFuture future = b.connect(new InetSocketAddress(host,port));
            Channel channel = future.awaitUninterruptibly().channel();

            setRunning(true);

            if (!future.isSuccess()) {
                LOGGER.warn("Error", future.cause());
            } else {
                try {
                    setChannel(new NettyChannel(channel));
                    setRunning(true);
                    while(isRunning()) {
                        try{ Thread.sleep(5000); } catch(Exception e) {}
                    }
                } catch(Exception e) {
                    LOGGER.warn("Error", e);
                }

                disconnect();
            }
        } catch(Exception e) {
            LOGGER.warn("Exception", e);
        } finally {
            b.group().shutdownGracefully();
        }
    }
}
