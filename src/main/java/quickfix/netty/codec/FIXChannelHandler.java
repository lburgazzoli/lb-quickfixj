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

package quickfix.netty.codec;

import org.apache.commons.lang.ObjectUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.netty.util.AbstractEventQueue;
import quickfix.netty.util.ChannelAttachment;
import quickfix.netty.FIXMessageEvent;
import quickfix.netty.FIXSession;
import quickfix.netty.FIXSessionType;
import quickfix.netty.util.IEventQueue;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class FIXChannelHandler extends SimpleChannelHandler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FIXChannelHandler.class);

    private final FIXSessionType m_sessionType;
    private final FIXSession m_session;
    private final IEventQueue<FIXMessageEvent> m_eventQueue;


    /**
     * c-tor
     *
     */
    public FIXChannelHandler(FIXSession session,FIXSessionType sessionType) {
        m_session     = session;
        m_sessionType = sessionType;

        m_eventQueue  = new AbstractEventQueue<FIXMessageEvent>() {
            @Override
            public boolean process(FIXMessageEvent data) {
                data.processMessage();
                return true;
            }
        };
    }

    /**
     *
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        if(!m_eventQueue.isRunning()) {

            m_eventQueue.start();

            ctx.getChannel().setAttachment(new ChannelAttachment(new HashMap<String,Object>() {{
                put(ChannelAttachment.SESSION_TYPE,m_sessionType);
                put(ChannelAttachment.SESSION     ,m_session);
            }}));

            if(m_sessionType == FIXSessionType.INITIATOR) {
                m_session.getSession().logon();
                m_session.getSession().next();

                m_session.startSessionTimer();
            }
        }

        ctx.sendUpstream(event);
    }

    /**
     *
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void channelDisconnected(
        ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {
        m_session.stopSessionTimer();
        m_eventQueue.stop();
        ctx.sendUpstream(event);
    }

    /**
     *
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void channelClosed(
        ChannelHandlerContext ctx, ChannelStateEvent event) throws Exception {

        m_eventQueue.stop();

        ctx.sendUpstream(event);
    }

    /**
     *
     * @param ctx
     * @param event
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
        if(event.getMessage() instanceof FIXMessageEvent) {
            if(m_eventQueue.isRunning()) {
                FIXMessageEvent mevt = (FIXMessageEvent)event.getMessage();
                if(ObjectUtils.equals(m_session.getSession().getSessionID(),mevt.getSession().getSessionID())) {
                    m_eventQueue.put((FIXMessageEvent)event.getMessage());
                } else {
                    LOGGER.warn("Bad SessionID, expected <{}>, got <{}>",
                        m_session.getSession().getSessionID(),mevt.getSession().getSessionID());
                }
            }
        }

        ctx.sendUpstream(event);
    }
}
