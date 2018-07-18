package com.renekon.starter;

import com.renekon.server.Server;
import com.renekon.server.connection.ConnectionManager;
import com.renekon.server.connection.NioConnectionManager;

import java.io.IOException;
import java.net.InetSocketAddress;

class StartNioServer {
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        ConnectionManager connectionManager = new NioConnectionManager(new InetSocketAddress(PORT));
        Server server = new Server(connectionManager, 10, 100);
        server.start(1);
    }
}
