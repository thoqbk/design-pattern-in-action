package com.garena.design.pattern.interceptor.impl;

import com.garena.design.pattern.SocketReader;
import com.garena.design.pattern.interceptor.AbstractInterceptor;
import com.garena.design.pattern.interceptor.Message;
import com.garena.design.pattern.interceptor.MessageContext;
import java.nio.ByteBuffer;

/**
 * Nov 2016
 *
 * @author Tho Q Luong
 */
public class BinaryInterceptor extends AbstractInterceptor {

    /**
     * Remove 4 bytes in header
     *
     * @param context
     */
    @Override
    public void messageReceived(MessageContext context) {
        byte[] originalBytes = (byte[]) context.getMessage().getContent();
        byte[] content = new byte[originalBytes.length - SocketReader.HEADER_LENGTH];
        System.arraycopy(originalBytes, SocketReader.HEADER_LENGTH, content, 0, content.length);
        context.next(new Message(content));
    }

    /**
     * Add 4 bytes of message length in header
     *
     * @param context
     */
    @Override
    public void writeRequested(MessageContext context) {
        byte[] content = (byte[]) context.getMessage().getContent();
        byte[] withHeader = ByteBuffer.allocate(content.length + SocketReader.HEADER_LENGTH)
                .putInt(content.length)
                .put(content)
                .array();
        context.next(new Message(withHeader));
    }

    @Override
    public String getName() {
        return "binary";
    }

}
