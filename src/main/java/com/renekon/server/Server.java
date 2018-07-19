package com.renekon.server;

import com.renekon.server.connection.ConnectionAcceptor;
import com.renekon.server.connection.ConnectionManager;
import com.renekon.server.connection.ConnectionProcessor;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.event.ConnectionEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        ExecutorService executorService = Executors.newFixedThreadPool(numProcessorThreads + 2);
        executorService.execute(connectionManager);

        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor(newConnectionQueue);
        executorService.execute(connectionAcceptor);

        ConcurrentHashMap<String, Connection> knownConnections = new ConcurrentHashMap<>();
        for (int t = 0; t < numProcessorThreads; t++) {
            ConnectionProcessor connectionProcessor = new ConnectionProcessor(connectionEventQueue, knownConnections);
            executorService.execute(connectionProcessor);
        }
    }
}
