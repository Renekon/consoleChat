package com.renekon.shared.connection.buffer;

public interface MessageBuffer {
    void put(byte[] data);

    byte[] getNextMessage();
}
