package com.renekon.client;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.buffer.MessageBuffer;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageFactory;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Client implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private Connection connection;

    private volatile boolean running;
    private volatile boolean acceptInput;

    private final Thread inputThread;


    private final MessageHandlerFactory messageHandlers = new MessageHandlerFactory();

    public Client(Connection connection) {
        this.connection = connection;
        this.inputThread = new Thread(() -> {
            while (acceptInput)
                sendMessage(MessageFactory.createUserTextMessage(connection.name, readInput()));
        });
        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        messageHandlers.register(MessageType.NAME_REQUEST, (message, connection) ->
                sendMessage(MessageFactory.createNameSentMessage(readInput())));
        messageHandlers.register(MessageType.NAME_ACCEPTED, (message, connection) -> {
            connection.name = message.getAuthor();
            startToAcceptInput();
        });
        messageHandlers.register(MessageType.NAME_SENT, (message, connection) -> sendMessage(message));
        messageHandlers.register(MessageType.DISCONNECT, (message, connection) -> stop());
    }

    private void sendMessage(Message message){
        connection.write(message.getBytes());
        writeToChannel();

    }

    private void startToAcceptInput() {
        if (!inputThread.isAlive()) {
            acceptInput = true;
            inputThread.start();
        }
    }

    public void run() {
        running = true;
        while (running) {
            if (connection.canRead())
                readFromChannel();
            else if (connection.canWrite())
                writeToChannel();
        }
        displayText("Disconnected from server. Input anything to exit.");
        stopToAcceptInput();
    }

    private void stopToAcceptInput() {
        acceptInput = false;
        while (inputThread.isAlive()) {
            try {
                inputThread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void readFromChannel() {
        try {
            connection.readFromChannel();
        } catch (IOException e) {
            stop();
        }

        MessageBuffer messageBuffer = connection.messageBuffer;
        messageBuffer.put(connection.getData());
        byte[] messageData = messageBuffer.getNextMessage();
        while (messageData != null) {
            Message message = MessageFactory.createFromBytes(messageData);
            if (message.getText() != null)
                displayText(message.getPrintText());
            MessageHandler handler = messageHandlers.get(message.getType());
            if (handler != null) {
                handler.execute(message, connection);
            }
            messageData = messageBuffer.getNextMessage();
        }
    }

    private void writeToChannel() {
        try {
            connection.writeToChannel();
        } catch (IOException e) {
            stop();
        }
    }

    private void displayText(String text) {
        System.out.println(text);
    }

    public String readInput() {
        return scanner.nextLine();
    }

    public void stop() {
        running = false;
    }
}
