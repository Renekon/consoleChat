package com.renekon.shared.connection;

import com.renekon.shared.connection.buffer.MessageBuffer;

import java.io.IOException;

public abstract class Connection {
    public volatile String name = null;
    public MessageBuffer messageBuffer = null;

    private volatile boolean shouldClose = false;

    public abstract boolean canRead();
    public abstract boolean canWrite();

    public abstract void write(byte[] data);
    public abstract byte[] getData();

    public abstract void writeToChannel() throws IOException;
    public abstract int readFromChannel() throws IOException;

    public void requestClose() {
        shouldClose = true;
    }
    public boolean shouldClose() {
        return shouldClose;
    }
}
