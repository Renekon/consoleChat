package com.renekon.server.connection;

import com.renekon.server.connection.event.ConnectionEvent;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;
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
        Thread.sleep(200);
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
        c.write(c.messageFactory.createNameRequestMessage());
        c.writeToChannel();
        Thread.sleep(200);
        ConnectionEvent event = connectionManager.connectionEvents.peek();
        Assertions.assertNotNull(event);
        Assertions.assertEquals(ConnectionEvent.Type.DATA, event.type);
    }

    @Test
    void closeConnection() throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress(5003);
        NioConnectionManager connectionManager = new NioConnectionManager(address);
        connectionManager.bindConnectionQueues(new ArrayBlockingQueue<>(1), new ArrayBlockingQueue<>(1));
        Thread selectionThread = new Thread(connectionManager, "connectionManager");
        selectionThread.start();
        Thread.sleep(300);
        Connection c = new NioSocketConnection(address);
        Assertions.assertTrue(((NioSocketConnection) c).channel.isOpen());
        connectionManager.close(c);
        Assertions.assertFalse(((NioSocketConnection) c).channel.isOpen());
    }
}
