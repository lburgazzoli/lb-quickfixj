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

package quickfix.transport.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.transport.FIXMessageEvent;
import quickfix.transport.FIXSession;
import quickfix.transport.FIXSessionType;
import quickfix.transport.util.AbstractEventQueue;
import quickfix.transport.util.IEventQueue;

/**
 *
 */
public class FIXChannelHandler extends SimpleChannelInboundHandler<FIXMessageEvent> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FIXChannelHandler.class);

    private final FIXSessionType m_sessionType;
    private final FIXSession m_session;
    private final IEventQueue<FIXMessageEvent> m_eventQueue;

    /**
     * c-tor
     *
     * @param session
     * @param sessionType
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

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(!m_eventQueue.isRunning()) {

            m_eventQueue.start();

            if(m_sessionType == FIXSessionType.INITIATOR) {
                m_session.getSession().logon();
                m_session.getSession().next();
                m_session.startSessionTimer();
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FIXMessageEvent msg) throws Exception {
        if(m_eventQueue.isRunning()) {
            if(ObjectUtils.equals(m_session.getSession().getSessionID(), msg.getSession().getSessionID())) {
                m_eventQueue.put(msg);
            } else {
                LOGGER.warn("Bad SessionID, expected <{}>, got <{}>",
                    m_session.getSession().getSessionID(),
                    msg.getSession().getSessionID());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }

    @Override
    public void channelInactive(
        ChannelHandlerContext ctx) throws Exception {
        m_session.stopSessionTimer();
        m_eventQueue.stop();
    }
}
