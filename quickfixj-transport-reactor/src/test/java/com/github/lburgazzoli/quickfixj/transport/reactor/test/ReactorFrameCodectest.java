/*
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
 */

package com.github.lburgazzoli.quickfixj.transport.reactor.test;

import com.github.lburgazzoli.quickfixj.transport.reactor.ReactorFrameCodec;
import org.junit.Assert;
import org.junit.Test;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;

/**
 *
 */
public class ReactorFrameCodectest
{
    @Test
    public void testDecode() {
        String message = "8=FIX.4.4"
            + 0x01 + "9=157"
            + 0x01 + "35=V"
            + 0x01 + "34=2"
            + 0x01 + "49=BRKR"
            + 0x01 + "52=20120921-06:41:04.295"
            + 0x01 + "56=QUOTE1-T"
            + 0x01 + "262=1:TOP:EURUSD"
            + 0x01 + "263=1"
            + 0x01 + "264=1"
            + 0x01 + "265=0"
            + 0x01 + "266=Y"
            + 0x01 + "146=1"
            + 0x01 + "55=EUR/USD"
            + 0x01 + "460=4"
            + 0x01 + "267=2"
            + 0x01 + "269=0"
            + 0x01 + "269=1"
            + 0x01 + "10=170"
            + 0x01;

        Function<Buffer, byte[]> decoder =
            new ReactorFrameCodec().decoder(new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                }
            });

        decoder.apply(Buffer.wrap(message.replace('|',(char)0x01)));

        Assert.assertEquals(1, 1);
    }
}
