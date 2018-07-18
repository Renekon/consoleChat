package com.renekon.shared.connection.buffer;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

public class ByteBufferCutter {
    public static byte[] cut(ByteBuffer buffer, byte stopByte) {
        int currentPosition = buffer.position();
        try {
            buffer.reset();
        } catch (InvalidMarkException e) {
            buffer.rewind();
            buffer.mark();
        }
        boolean messageFound = false;
        while (buffer.position() < currentPosition) {
            if (buffer.get() == stopByte) {
                messageFound = true;
                break;
            }
        }

        if (!messageFound) {
            buffer.mark();
            return null;
        }

        byte[] bytes = new byte[buffer.position()];
        buffer.position(currentPosition);
        buffer.flip();
        buffer.get(bytes);
        buffer.compact();

        return bytes;
    }
}
