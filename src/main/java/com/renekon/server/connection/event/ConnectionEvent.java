package com.renekon.server.connection.event;

import com.renekon.shared.connection.Connection;

abstract public class ConnectionEvent {
    public enum Type {DATA, CLOSE}

    public final Type type;
    public final Connection connection;

    ConnectionEvent(Type type, Connection connection) {
        this.type = type;
        this.connection = connection;
    }
}
