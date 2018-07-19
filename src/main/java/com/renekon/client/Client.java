package com.renekon.client;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private Connection connection;

    private volatile boolean running;

    private ScheduledExecutorService executorService;

    private final MessageHandlerFactory messageHandlers = new MessageHandlerFactory();

    public Client(Connection connection) {
        this.connection = connection;
        executorService = Executors.newSingleThreadScheduledExecutor();

        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        messageHandlers.register(MessageType.NAME_REQUEST, (message, connection) ->
                sendMessage(connection.messageFactory.createNameSentMessage(readInput())));
        messageHandlers.register(MessageType.NAME_ACCEPTED, (message, connection) -> {
            if (connection.name == null)
                startInputScanner();
            connection.name = message.getAuthor();
        });
        messageHandlers.register(MessageType.NAME_SENT, (message, connection) -> sendMessage(message));
        messageHandlers.register(MessageType.DISCONNECT, (message, connection) -> stop());
    }

    private void sendMessage(Message message) {
        connection.write(message);
        writeToChannel();
    }

    private void startInputScanner() {
        executorService.scheduleWithFixedDelay(() -> {
            if (scanner.hasNext())
                sendMessage(connection.messageFactory.createUserTextMessage(connection.name, readInput()));
        }, 100, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            if (connection.canRead())
                readFromChannel();
        }
        displayText("Disconnected from server");
        executorService.shutdown();
        System.exit(0);
    }

    private void readFromChannel() {
        try {
            connection.readFromChannel();
        } catch (IOException e) {
            stop();
        }
        List<Message> messages = connection.readMessages();
        for (Message message : messages) {
            if (message.getText() != null)
                displayText(message.getAuthor() + ": " + message.getText());
            MessageHandler handler = messageHandlers.get(message.getType());
            if (handler != null) {
                handler.execute(message, connection);
            }
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

    String readInput() {
        return scanner.nextLine();
    }

    public void stop() {
        running = false;
    }
}
