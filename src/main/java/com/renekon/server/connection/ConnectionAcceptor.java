package com.renekon.server.connection;

import com.renekon.shared.connection.Connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionAcceptor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ConnectionAcceptor.class.getName());

    private final ArrayBlockingQueue<Connection> newConnections;

    public ConnectionAcceptor(ArrayBlockingQueue<Connection> newConnections) {
        this.newConnections = newConnections;
    }

    @Override
    public void run() {
        Connection connection;
        while (true) {
            try {
                connection = newConnections.take();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupting while taking connection from queue", e);
                continue;
            }
            connection.write(Connection.messageFactory.createServerTextMessage("Hello! What is your name?"));
            connection.write(Connection.messageFactory.createNameRequestMessage());
        }
    }
}
