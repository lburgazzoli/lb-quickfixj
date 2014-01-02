package com.github.lburgazzoli.quickfixj.transport.netty.test;

import com.github.lburgazzoli.quickfixj.transport.netty.codec.NettyMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class NettyFrameCodecTest extends NettyTestSupport {

    // *************************************************************************
    //
    // *************************************************************************

    @Test
    public void testDecode() {
        ByteBuf message = new MessageBuilder()
            .add(  "8","FIX.4.2")
            .add(  "9","69")
            .add( "35","A")
            .add( "34","1")
            .add( "49","TEST")
            .add( "52","20140102-11:55:47.746")
            .add( "56","EXEC")
            .add( "98","0")
            .add("108","30")
            .add("141","Y")
            .add( "10","151")
            .build();

        ByteBuf         msgcopy = message.copy();
        EmbeddedChannel channel = new EmbeddedChannel(new NettyMessageDecoder());

        // raw write write
        Assert.assertTrue(channel.writeInbound(message));
        Assert.assertTrue(channel.finish());

        // read
        byte[] result = (byte[])channel.readInbound();
        Assert.assertNotNull(result);

        if(msgcopy.hasArray()) {
            Assert.assertArrayEquals(msgcopy.array(),result);
        }
    }
}
