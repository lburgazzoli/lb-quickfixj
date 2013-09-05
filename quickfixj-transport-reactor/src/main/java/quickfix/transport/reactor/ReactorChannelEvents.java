package quickfix.transport.reactor;

import reactor.event.Event;
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
}
