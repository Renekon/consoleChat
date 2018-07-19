package com.renekon.shared.connection;

import com.renekon.server.Server;
import com.renekon.server.connection.NioConnectionManager;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

class NioSocketConnectionTest {
    @Test
    void canRead() throws IOException {
        InetSocketAddress address = new InetSocketAddress(4000);
        Server s = new Server(new NioConnectionManager(address), 10, 10);
        s.start(1);
        Connection c = new NioSocketConnection(address);
        Assertions.assertTrue(c.canRead());
    }

    @Test
    void write() throws IOException {
        InetSocketAddress address = new InetSocketAddress(4001);
        Server s = new Server(new NioConnectionManager(address), 10, 10);
        s.start(1);
        Connection c = new NioSocketConnection(address);
        Assertions.assertTrue(((NioSocketConnection) c).nothingToWrite());
        c.write(c.messageFactory.createNameRequestMessage());
        Assertions.assertFalse(((NioSocketConnection) c).nothingToWrite());
    }

    @Test
    void readData() throws IOException, InterruptedException {
        InetSocketAddress address = new InetSocketAddress(4002);
        Server s = new Server(new NioConnectionManager(address), 10, 10);
        s.start(1);

        Connection c = new NioSocketConnection(address);
        List<Message> msg = c.readMessages();
        Assertions.assertEquals(msg.size(), 0);
        Thread.sleep(500);
        c.readFromChannel();
        msg = c.readMessages();
        Assertions.assertNotEquals(msg.size(), 0);
    }

    @Test
    void writeToChannel() throws IOException {
        InetSocketAddress address = new InetSocketAddress(4003);
        Server s = new Server(new NioConnectionManager(address), 10, 10);
        s.start(1);
        Connection c = new NioSocketConnection(address);

        c.write(c.messageFactory.createNameRequestMessage());
        Assertions.assertFalse(((NioSocketConnection) c).nothingToWrite());
        c.writeToChannel();
        Assertions.assertTrue(((NioSocketConnection) c).nothingToWrite());
    }
}