package com.renekon.server.connection;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;
import com.renekon.shared.connection.event.ConnectionEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

class NioConnectionManagerTest {

    @Test
    void accept() throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress(5001);
        NioConnectionManager connectionManager = new NioConnectionManager(address);
        connectionManager.bindConnectionQueues(new ArrayBlockingQueue<>(1), new ArrayBlockingQueue<>(1));
        Thread selectionThread = new Thread(connectionManager, "connectionManager");
        selectionThread.start();
        int keys = connectionManager.selector.keys().size();
        new NioSocketConnection(address);
        Thread.sleep(500);
        int keysAfter = connectionManager.selector.keys().size();
        Assertions.assertEquals(keysAfter - keys, 1);
    }

    @Test
    void read() throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress(5002);
        NioConnectionManager connectionManager = new NioConnectionManager(address);
        connectionManager.bindConnectionQueues(new ArrayBlockingQueue<>(1), new ArrayBlockingQueue<>(1));
        Thread selectionThread = new Thread(connectionManager, "connectionManager");
        selectionThread.start();
        Connection c = new NioSocketConnection(address);
        c.write(new byte[]{1, 2, 3});
        c.writeToChannel();
        Thread.sleep(500);
        ConnectionEvent event = connectionManager.connectionEvents.peek();
        Assertions.assertEquals(ConnectionEvent.Type.DATA, event.type);
    }
}
