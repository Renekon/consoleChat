package com.renekon.starter;

import com.renekon.server.Server;
import com.renekon.server.connection.ConnectionManager;
import com.renekon.server.connection.NioConnectionManager;

import java.io.IOException;
import java.net.InetSocketAddress;

class NioServerStarter {
    private static final int PORT = 5000;
    private static final int CONNECT_QUEUE_CAPACITY = 100;
    private static final int CONNECT_EVENT_QUEUE_CAPACITY = 100;
    private static final int PROCESSOR_THREADS_NUM = 1;

    public static void main(String[] args) throws IOException {
        ConnectionManager connectionManager = new NioConnectionManager(new InetSocketAddress(PORT));
        Server server = new Server(connectionManager, CONNECT_QUEUE_CAPACITY, CONNECT_EVENT_QUEUE_CAPACITY);
        server.start(PROCESSOR_THREADS_NUM);
        System.out.println("Server started");
    }
}
