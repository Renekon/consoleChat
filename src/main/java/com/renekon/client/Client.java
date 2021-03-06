package com.renekon.client;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private Connection connection;

    volatile boolean running;

    ScheduledExecutorService executorService;

    final MessageHandlerFactory messageHandlers = new MessageHandlerFactory();

    public Client(Connection connection) {
        this.connection = connection;
        executorService = Executors.newSingleThreadScheduledExecutor();

        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        messageHandlers.register(MessageType.NAME_REQUEST, (message, connection) ->
                sendMessage(Connection.messageFactory.createNameSentMessage(readInput())));
        messageHandlers.register(MessageType.NAME_ACCEPTED, (message, connection) -> {
            if (connection.name == null)
                startInputScanner();
            connection.name = message.getAuthor();
        });
        messageHandlers.register(MessageType.NAME_SENT, (message, connection) -> sendMessage(message));
        messageHandlers.register(MessageType.DISCONNECT, (message, connection) -> connection.requestClose());
    }

    private void sendMessage(Message message) {
        connection.write(message);
        writeToChannel();
    }

    void startInputScanner() {
        executorService.scheduleWithFixedDelay(() -> {
            if (connection.shouldClose())
                stop();
            if (hasInput())
                sendMessage(Connection.messageFactory.createUserTextMessage(connection.name, readInput()));
        }, 100, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            if (connection.shouldClose())
                stop();
            if (connection.canRead())
                readFromChannel();
        }
    }

    private void readFromChannel() {
        try {
            connection.readFromChannel();
        } catch (IOException e) {
            stop();
        }
        for (Message message : connection.readMessages()) {
            handleMessage(message);
        }
    }

    private void handleMessage(Message message) {
        if (message.getText() != null)
            displayText(message.getAuthor() + ": " + message.getText());
        MessageHandler handler = messageHandlers.get(message.getType());
        if (handler != null) {
            handler.execute(message, connection);
        }
    }

    private void writeToChannel() {
        try {
            connection.writeToChannel();
        } catch (IOException e) {
            stop();
        }
    }

    void displayText(String text) {
        System.out.println(text);
    }

    boolean hasInput() {
        return scanner.hasNext();
    }

    String readInput() {
        return scanner.nextLine();
    }

    void stop() {
        running = false;
        executorService.shutdown();
        System.exit(0);
    }
}
