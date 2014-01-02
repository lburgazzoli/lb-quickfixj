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
package com.github.lburgazzoli.quickfixj.transport.netty.test;

import com.github.lburgazzoli.quickfixj.transport.FIXCodecHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 */
public class NettyTestSupport {
    /**
     *
     */
    public static class MessageBuilder {
        private ByteBuf m_buffer;

        /**
         * c-tor
         */
        public MessageBuilder() {
            m_buffer = Unpooled.buffer();
        }

        /**
         *
         * @param key
         * @param value
         */
        public MessageBuilder add(String key,String value) {
            m_buffer.writeBytes(key.getBytes());
            m_buffer.writeByte(FIXCodecHelper.BYTE_EQUALS);
            m_buffer.writeBytes(value.getBytes());
            m_buffer.writeByte(FIXCodecHelper.BYTE_SOH);

            return this;
        }

        /**
         *
         * @return
         */
        public ByteBuf build() {
            return m_buffer;
        }
    }
}
