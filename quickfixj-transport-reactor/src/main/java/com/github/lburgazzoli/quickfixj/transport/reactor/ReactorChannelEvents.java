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
package com.github.lburgazzoli.quickfixj.transport.reactor;

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
