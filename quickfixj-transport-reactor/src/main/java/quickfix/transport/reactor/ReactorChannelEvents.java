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

package quickfix.transport.reactor;

import reactor.event.Event;
import reactor.function.Consumer;
import reactor.tcp.TcpConnection;

/**
 *
 */
public class ReactorChannelEvents {

    // *************************************************************************
    //
    // *************************************************************************

    public static class DataHolder<T> {
        private final T m_data;

        /**
         * c-tor
         *
         * @param data
         */
        protected DataHolder(final T data) {
            m_data = data;
        }

        /**
         *
         * @return
         */
        public final T get() {
            return m_data;
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static final class ConnectionUp
        extends DataHolder<TcpConnection<byte[],byte[]>> {
        /**
         * c-tor
         *
         * @param data
         */
        private ConnectionUp(final TcpConnection<byte[],byte[]> data) {
            super(data);
        }

        /**
         *
         * @param data
         * @return
         */
        public static Event<ConnectionUp> wrap(TcpConnection<byte[],byte[]> data) {
            return Event.wrap(new ConnectionUp(data));
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static final class ConnectionDown extends DataHolder<TcpConnection<byte[],byte[]>> {
        private ConnectionDown(final TcpConnection<byte[],byte[]> data) {
            super(data);
        }

        /**
         *
         * @param data
         * @return
         */
        public static Event<ConnectionDown> wrap(TcpConnection<byte[],byte[]> data) {
            return Event.wrap(new ConnectionDown(data));
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static final class Data extends DataHolder<byte[]> {
        private Data(final byte[] data) {
            super(data);
        }

        /**
         *
         * @param data
         * @return
         */
        public static Event<Data> wrap(byte[] data) {
            return Event.wrap(new Data(data));
        }
    }


    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    public static class ConnectionUpConsumer implements Consumer<Event<ReactorChannelEvents.ConnectionUp>> {
        public void accept(final Event<ReactorChannelEvents.ConnectionUp> data) {
        }
    }

    /**
     *
     */
    public static class ConnectionDownConsumer implements Consumer<Event<ReactorChannelEvents.ConnectionDown>> {
        public void accept(final Event<ReactorChannelEvents.ConnectionDown> data) {
        }
    }

    /**
     *
     */
    public static class DataConsumer implements Consumer<Event<ReactorChannelEvents.Data>> {
        public void accept(final Event<ReactorChannelEvents.Data> data) {
        }
    }
}
