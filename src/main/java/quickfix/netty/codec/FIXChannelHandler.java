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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Session;
import quickfix.netty.FIXChannelAttachment;
import quickfix.netty.FIXMessageEvent;
import quickfix.netty.FIXSessionType;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class FIXChannelHandler extends SimpleChannelHandler implements Runnable{

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FIXChannelHandler.class);

    private FIXSessionType m_sessionType;
    private Session m_session;
    private BlockingQueue<FIXMessageEvent> m_eventQueue;
    private AtomicBoolean m_running;
    private Thread m_eventThread;


    /**
     * c-tor
     *
     */
    public FIXChannelHandler(Session session,FIXSessionType sessionType) {
        m_session     = session;
        m_sessionType = sessionType;
        m_eventQueue  = new LinkedBlockingQueue<FIXMessageEvent>();
        m_eventThread = null;
        m_running     = new AtomicBoolean(false);
    }

    /**
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        m_running.set(true);

        m_eventThread = new Thread(this);
        m_eventThread.start();

        ctx.getChannel().setAttachment(new FIXChannelAttachment(new HashMap<String,Object>() {{
            put(FIXChannelAttachment.SESSION_TYPE,m_sessionType);
            put(FIXChannelAttachment.SESSION     ,m_session);
        }}));

        if(m_session != null && m_sessionType == FIXSessionType.INITIATOR) {
            m_session.logon();
            m_session.next();
        }

        ctx.sendUpstream(e);
    }

    /**
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ctx.sendUpstream(e);
    }

    /**
     *
     */
    public void run() {
        while(m_running.get()) {
            try {
                FIXMessageEvent em = m_eventQueue.poll(1000L,TimeUnit.MILLISECONDS);
                if (em != null) {
                    em.processMessage();
                } else {
                    LOGGER.debug("No message");
                }
            } catch(InterruptedException e) {
            }
        }
    }
}
