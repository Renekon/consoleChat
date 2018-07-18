package com.renekon.shared.connection.event;

import com.renekon.shared.connection.Connection;

public class CloseConnectionEvent extends ConnectionEvent {
    public CloseConnectionEvent(Connection connection) {
        super(Type.CLOSE, connection);
    }
}
