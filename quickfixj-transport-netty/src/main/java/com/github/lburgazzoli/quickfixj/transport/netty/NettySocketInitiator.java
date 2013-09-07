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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.SessionID;
import com.github.lburgazzoli.quickfixj.transport.AbstractTransport;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionHelper;
import com.github.lburgazzoli.quickfixj.transport.FIXSessionType;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class NettySocketInitiator extends AbstractTransport implements INettyStateHandler {
    private static Logger LOGGER =
        LoggerFactory.getLogger(NettySocketInitiator.class);

    private Bootstrap m_boot;

    /**
     * c-tor
     *
     * @param session
     */
    public NettySocketInitiator(FIXSessionHelper session) {
        super(session);

        m_boot = null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    @Override
    public void run() {
        try {
            m_boot = new Bootstrap();
            m_boot.group(new NioEventLoopGroup());
            m_boot.channel(NioSocketChannel.class);
            m_boot.option(ChannelOption.SO_KEEPALIVE, true);
            m_boot.option(ChannelOption.TCP_NODELAY, true);
            m_boot.handler(new NettyChannelInitializer(this,getHelper(), FIXSessionType.INITIATOR));

            SessionID sid  = getHelper().getSession().getSessionID();
            String    host = getHelper().getSettings().getString(sid, "SocketConnectHost");
            int       port = getHelper().getSettings().getInt(sid, "SocketConnectPort");

            m_boot.remoteAddress(new InetSocketAddress(host,port));

            if(!isRunning()) {
                setRunning(true);
                connect();
            }
        } catch(Exception e) {
            LOGGER.warn("Exception", e);
            setRunning(false);
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if(m_boot != null) {
            m_boot.group().shutdownGracefully();
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    private void connect() {
        ChannelFuture future = m_boot.connect();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if(future.isDone() && future.isSuccess()) {
                    setChannel(new NettyChannel(future.channel()));
                } else if(!future.isSuccess() && !future.isCancelled()) {
                    LOGGER.warn("Error", future.cause());
                    reconnect();
                }
            }
        });
    }

    private void reconnect() {
        if(isRunning() && m_boot != null) {
            Runnable task = new Runnable() {
                public void run() {
                    connect();
                }
            };

            m_boot.group().schedule(task,5,TimeUnit.SECONDS);
       }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void onConnect(Channel channel) {
    }

    @Override
    public void onDisconnect(Channel channel) {
        reconnect();
    }
}
