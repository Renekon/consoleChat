package com.renekon.shared.connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

class NioSocketConnectionTest {

    @Test
    void write() throws IOException {
        InetSocketAddress address = new InetSocketAddress(4001);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        serverChannel.configureBlocking(false);
        Connection c = new NioSocketConnection(address);
        Assertions.assertTrue(((NioSocketConnection) c).nothingToWrite());
        c.write(Connection.messageFactory.createNameRequestMessage());
        Assertions.assertFalse(((NioSocketConnection) c).nothingToWrite());
    }


    @Test
    void writeToChannel() throws IOException {
        InetSocketAddress address = new InetSocketAddress(4003);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        serverChannel.configureBlocking(false);
        Connection c = new NioSocketConnection(address);

        c.write(Connection.messageFactory.createNameRequestMessage());
        Assertions.assertFalse(((NioSocketConnection) c).nothingToWrite());
        c.writeToChannel();
        Assertions.assertTrue(((NioSocketConnection) c).nothingToWrite());
    }
}
