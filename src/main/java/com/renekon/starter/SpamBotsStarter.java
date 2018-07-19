package com.renekon.starter;

import com.renekon.client.Client;
import com.renekon.client.SpamBot;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SpamBotsStarter {
    private static final int PORT = 5000;
    private static final int DEFAULT_BOT_COUNT = 1000;
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException {
        executorService = Executors.newCachedThreadPool();
        runSpamBotThreads(getBotCount(args));
        startReadingInput();
    }

    private static int getBotCount(String[] args) {
        if (args.length == 1)
            return Integer.valueOf(args[0]);
        else
            return DEFAULT_BOT_COUNT;
    }

    private static void runSpamBotThreads(int botCount) throws IOException {
        for (int i = 0; i < botCount; ++i) {
            Connection connection = new NioSocketConnection(new InetSocketAddress(PORT));
            Client client = new SpamBot(connection);
            executorService.execute(client);
        }
    }

    private static void startReadingInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bots started, press q to stop");
        while (!executorService.isShutdown()) {
            String input = scanner.nextLine();
            if ("q".equals(input))
                System.exit(0);
        }
    }
}
