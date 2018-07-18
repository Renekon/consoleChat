package com.renekon.starter;

import com.renekon.client.Client;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;

import java.net.InetSocketAddress;

class StartDemoClient {
    private static final int PORT = 5000;

    static public void main(String[] args) throws Exception {
        Connection connection = new NioSocketConnection(new InetSocketAddress(PORT));
        Client client = new Client(connection);
        client.run();
    }
}
