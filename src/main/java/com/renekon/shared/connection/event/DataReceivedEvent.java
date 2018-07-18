package com.renekon.shared.connection.event;

import com.renekon.shared.connection.Connection;

public class DataReceivedEvent extends ConnectionEvent {
    public final byte[] data;

    public DataReceivedEvent(Connection connection) {
        super(Type.DATA, connection);
        data = connection.readData();
    }
}
