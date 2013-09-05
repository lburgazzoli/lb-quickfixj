package quickfix.transport.reactor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;
import reactor.tcp.encoding.Codec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ReactorFrameCodec implements Codec<Buffer,byte[],byte[]> {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ReactorFrameCodec.class);

    private static final int    MSG_MIN_CHARS = 30;
    private static final String MSG_BEGIN     = "8=FIX";
    private static final String REX_HEADER    = "(8=FIX[T]?\\.[0-9]\\.[0-9])\\x01(9=[0-9]+)\\x01";
    private static final String REX_TRAILER   = "\\x01(10=[0-9]+)\\x01";
    private static final char   CHAR_EQUALS   = '=';
    private static final char   CHAR_SOH      = 0x01;

    /**
     * c-tor
     */
    public ReactorFrameCodec() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public Function<Buffer, byte[]> decoder(final Consumer<byte[]> next) {
        return new Decoder(next);
    }

    @Override
    public Function<byte[], Buffer> encoder() {
        return new Encoder();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     * Decoder
     */
    private final class Decoder implements Function<Buffer,byte[]> {
        private final Consumer<byte[]> m_next;

        /**
         *
         * @param next
         */
        public Decoder(final Consumer<byte[]> next) {
            m_next = next;
        }

        @Override
        public byte[] apply(Buffer buffer) {
            //LOGGER.debug("Decoder.Buffer - 1 - position={}, limit={}, remaining={}",
            //    buffer.position(),buffer.limit(),buffer.remaining());

            byte[]  rv    = null;
            boolean reset = true;

            if(buffer.remaining() >= MSG_MIN_CHARS) {
                // keep track of the initial state fo the buffer
                int pos  = buffer.position();
                int lim  = buffer.limit();

                String  bs = buffer.asString();
                Matcher mh = getMatcherFor(REX_HEADER,bs);

                // reset the buffer
                buffer.position(pos);
                buffer.limit(lim);

                // Extract following FIX tags:
                // -  8 BeginString
                // -  9 BodyLength
                // - 10 CheckSum
                if(mh.find() && (mh.groupCount() == 2)) {
                    int offset = mh.start(0);
                    int hlen   = mh.end(0) - mh.start(0);
                    int blen   = getValueAsInt(mh.group(2));
                    int tbegin = offset + hlen + blen;
                    int tend   = -1;

                    if((buffer.remaining() - offset - hlen) > blen) {
                        buffer.position(tbegin-1);
                        if( buffer.read() == 0x01 && buffer.read() == '1'  &&
                            buffer.read() == '0'  && buffer.read() == '='  ) {
                            for(int i=tbegin+4;buffer.remaining() > 0;i++) {
                                if(buffer.read() == CHAR_SOH) {
                                    tend = i;
                                    break;
                                }
                            }

                            if(tend != -1) {
                                buffer.position(pos);
                                buffer.limit(lim);

                                // csum     is used to check msg correctness
                                // msgSize  is used to retrieve the whole message
                                // bodySize is used for checksum
                                int csum     = Buffer.parseInt(buffer,tbegin+3,tend-1);
                                int msgSize  = tend   - offset - 1;
                                int bodySize = tbegin - offset;

                                if(msgSize > 0) {
                                    // reset the bufefr and skip to the beinning of
                                    // the FIX message (tag 8)
                                    buffer.position(pos);
                                    buffer.limit(lim);
                                    buffer.skip(offset);

                                    if(buffer.remaining() >= msgSize) {
                                        int begin = buffer.position();
                                        int end   = buffer.position() + msgSize;

                                        // extract the message as byte array an
                                        // flag the buffer as "consumed"
                                        rv    = buffer.createView(begin,end+1).get().asBytes();
                                        reset = false;

                                        if(!validateCheckSum(rv,bodySize,csum)) {
                                            //TODO: drop connection
                                        }

                                        // "consume" the buffer and leave partial
                                        // messages in for further processing
                                        buffer.position(offset+msgSize+1);
                                        buffer.limit(lim);
                                    }
                                }
                            }
                        }
                    }
                }

                if(reset) {
                    buffer.position(pos);
                    buffer.limit(lim);
                }
            }

            //LOGGER.debug("Decoder.Buffer - 2 - position={}, limit={}, remaining={}",
            //    buffer.position(),buffer.limit(),buffer.remaining());

            return next(rv);
        }

        // **********************************************************************
        //
        // **********************************************************************

        /**
         *
         * @param data
         * @param dataLen
         * @param expectedCheckSum
         * @return
         */
        private boolean validateCheckSum(byte[] data,int dataLen,int expectedCheckSum) {
            int sum = calculateCheckSum(data,dataLen);

            if(expectedCheckSum == sum) {
                return true;
            } else {
                LOGGER.warn("Checksum KO - calc={},rec={}",sum,expectedCheckSum);
                return false;
            }
        }

        /**
         *
         * @param buffer
         * @return
         */
        private byte[] next(byte[] buffer) {
            if(null != m_next) {
                m_next.accept(buffer);
                return null;
            } else {
                return buffer;
            }
        }

        /**
         *
         * @param pattern
         * @param data
         * @return
         */
        private Matcher getMatcherFor(String pattern, CharSequence data) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(data);

            return m;
        }

        /**
         *
         * @param kv
         * @return
         */
        private int getValueAsInt(String kv) {
            String[] kv1 = StringUtils.split(kv, CHAR_EQUALS);
            return (kv1.length == 2) ? Integer.parseInt(kv1[1]) : -1;
        }

        /**
         *
         * @param data
         * @param dataLen
         * @return
         */
        private int calculateCheckSum(byte[] data, int dataLen) {
            int sum = 0;
            for (int i = 0; i < dataLen; i++) {
                sum += data[i];
            }

            return sum % 256;
        }
    }

    /**
     * Encoder
     */
    private final class Encoder implements Function<byte[], Buffer> {
        @Override
        public Buffer apply(byte[] buffer) {
            if (null == buffer) {
                return null;
            } else if (buffer.length == 0) {
                return null;
            }

            return Buffer.wrap(buffer);
        }
    }
}
