package com.renekon.server.connection.event;

import com.renekon.shared.connection.Connection;

public class CloseConnectionEvent extends ConnectionEvent {
    public CloseConnectionEvent(Connection connection) {
        super(Type.CLOSE, connection);
    }
}
