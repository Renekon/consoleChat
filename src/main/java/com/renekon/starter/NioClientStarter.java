package com.renekon.starter;

import com.renekon.client.Client;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.NioSocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.util.Scanner;

class NioClientStarter {
    private static final int PORT = 5000;
    private static Scanner scanner = new Scanner(System.in);

    static public void main(String[] args) {
        printToConsole("Enter hostname to connect");
        boolean connecting = true;
        while (connecting) {
            try {
                Connection connection = new NioSocketConnection(new InetSocketAddress(scanner.nextLine(), PORT));
                Client client = new Client(connection);
                client.run();
                connecting = false;
            } catch (UnresolvedAddressException e) {
                printToConsole("Address is not valid, try another one");
            } catch (IOException e) {
                printToConsole("Connection refused, try again");
            }
        }
    }

    private static void printToConsole(String s) {
        System.out.println(s);
    }
}
