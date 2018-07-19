package com.renekon.server.connection.event;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.Message;

import java.util.List;

public class DataReceivedEvent extends ConnectionEvent {
    public final List<Message> messages;

    public DataReceivedEvent(Connection connection) {
        super(Type.DATA, connection);
        messages = connection.readMessages();
    }
}
