package com.renekon.server;

import com.renekon.server.connection.ConnectionManager;
import com.renekon.server.connection.ConnectionAcceptor;
import com.renekon.server.connection.ConnectionProcessor;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.event.ConnectionEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final ConnectionManager connectionManager;
    private final ArrayBlockingQueue<Connection> newConnectionQueue;
    private final ArrayBlockingQueue<ConnectionEvent> connectionEventQueue;


    public Server(ConnectionManager connectionManager, int newConnectionQueueCapacity, int connectionEventQueueCapacity) {
        this.connectionManager = connectionManager;
        newConnectionQueue = new ArrayBlockingQueue<>(newConnectionQueueCapacity);
        connectionEventQueue = new ArrayBlockingQueue<>(connectionEventQueueCapacity);
    }

    public void start(int numProcessorThreads) {
        connectionManager.bindConnectionQueues(newConnectionQueue, connectionEventQueue);
        Thread selectionThread = new Thread(connectionManager, "connectionManager");
        selectionThread.start();

        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor(newConnectionQueue);
        Thread connectionAcceptorThread = new Thread(connectionAcceptor, "ConnectionAcceptor");
        connectionAcceptorThread.start();

        ConcurrentHashMap<String, Connection> knownConnections = new ConcurrentHashMap<>();
        for (int t = 0; t < numProcessorThreads; ++t) {
            ConnectionProcessor connectionProcessor = new ConnectionProcessor(connectionEventQueue, knownConnections);
            Thread connectionProcessorThread = new Thread(connectionProcessor, "ConnectionProcessor-" + t);
            connectionProcessorThread.start();
        }
    }
}
