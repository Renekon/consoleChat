package com.renekon.shared.connection.buffer;

import com.renekon.shared.message.Message;

import java.nio.ByteBuffer;

public class NioMessageBuffer {
    private static final int INITIAL_CAPACITY = 128;
    private static final int RESIZE_FACTOR = 2;
    private ByteBuffer buffer;

    public NioMessageBuffer() {
        buffer = ByteBuffer.allocate(INITIAL_CAPACITY);
    }

    public void put(byte[] data) {
        int requiredCapacity = buffer.position() + data.length;
        if (requiredCapacity > buffer.capacity()) {
            ByteBuffer newBuffer = ByteBuffer.allocate(Math.max(RESIZE_FACTOR * buffer.capacity(), requiredCapacity));
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
        buffer.put(data);
    }

    public byte[] getNextMessage() {
        return ByteBufferCutter.cut(buffer, Message.MESSAGE_END);
    }
}
