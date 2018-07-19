package com.renekon.server.connection;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.event.ConnectionEvent;

import java.util.concurrent.ArrayBlockingQueue;

public interface ConnectionManager extends Runnable {

    void bindConnectionQueues(ArrayBlockingQueue<Connection> newConnectionQueue,
                              ArrayBlockingQueue<ConnectionEvent> connectionEventsQueue);

    void accept();

    void read();

    void write();

    void close(Connection connection);
}
