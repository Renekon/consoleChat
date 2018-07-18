package com.renekon.server.chat;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.event.ConnectionEvent;
import com.renekon.server.Server;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Chat {
    private final Server server;
    private final int newConnectionQueueCapacity;
    private final int connectionEventQueueCapacity;

    public Chat(Server server, int newConnectionQueueCapacity, int connectionEventQueueCapacity) {
        this.server = server;
        this.newConnectionQueueCapacity = newConnectionQueueCapacity;
        this.connectionEventQueueCapacity = connectionEventQueueCapacity;
    }

    public void start(int numProcessorThreads) throws IOException {
        ArrayBlockingQueue<Connection> newConnectionQueue = new ArrayBlockingQueue<>(newConnectionQueueCapacity);
        ArrayBlockingQueue<ConnectionEvent> connectionEventQueue
                = new ArrayBlockingQueue<>(connectionEventQueueCapacity);

        server.bindConnectionQueues(newConnectionQueue, connectionEventQueue);
        server.start();

        ConcurrentHashMap<String, Connection> knownConnections = new ConcurrentHashMap<>();
        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor(newConnectionQueue);
        Thread connectionAcceptorThread = new Thread(connectionAcceptor, "ConnectionAcceptor");
        connectionAcceptorThread.start();

        for (int t = 0; t < numProcessorThreads; ++t) {
            ConnectionProcessor connectionProcessor = new ConnectionProcessor(connectionEventQueue, knownConnections);
            Thread connectionProcessorThread = new Thread(connectionProcessor, "ConnectionProcessor-" + t);
            connectionProcessorThread.start();
        }
    }
}
