package com.renekon.starter;

import com.renekon.client.Client;
import com.renekon.client.SpamBot;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

class StartLoadTest {
    private static final int PORT = 5000;
    private static final int DEFAULT_BOT_COUNT = 2000;

    public static void main(String[] args) throws IOException {
        int botCount;
        if (args.length == 1) {
            botCount = Integer.valueOf(args[0]);
        } else {
            botCount = DEFAULT_BOT_COUNT;
        }

        ArrayList<Client> clients = new ArrayList<>();
        for (int i = 0; i < botCount; ++i) {
            Connection connection = new NioSocketConnection(new InetSocketAddress(PORT));
            Client client = new SpamBot(connection);
            clients.add(client);
            Thread t = new Thread(client);
            t.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Client client : clients) {
                client.stop();
            }
        }));
    }
}
