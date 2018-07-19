package com.renekon.shared.connection;

import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageFactory;

import java.io.IOException;
import java.util.List;

public abstract class Connection {
    public MessageFactory messageFactory;

    public volatile String name = null;

    private volatile boolean shouldClose = false;

    public abstract boolean canRead();

    public abstract void write(Message message);

    public abstract List<Message> readMessages();

    public abstract void writeToChannel() throws IOException;

    public abstract void readFromChannel() throws IOException;

    public void requestClose() {
        shouldClose = true;
    }

    public boolean shouldClose() {
        return shouldClose;
    }
}
