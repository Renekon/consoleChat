package com.renekon.starter;

import com.renekon.client.Client;

import java.net.InetSocketAddress;

class StartDemoClient {
    private static final int PORT = 5000;

    static public void main(String[] args) throws Exception {
        Client client = new Client();
        client.connect(new InetSocketAddress(PORT));
        client.run();
    }
}
