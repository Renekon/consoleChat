package com.renekon.client;

import com.renekon.shared.connection.ModeChangeRequestQueue;
import com.renekon.shared.connection.NioSocketConnection;
import com.renekon.shared.connection.buffer.ChatMessageBuffer;
import com.renekon.shared.connection.buffer.MessageBuffer;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageFactory;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private NioSocketConnection connection;
    private Selector selector;

    private volatile boolean running;
    private volatile boolean acceptInput;

    private final Thread inputThread;


    private final MessageHandlerFactory messageHandlers = new MessageHandlerFactory();
    private ModeChangeRequestQueue modeChangeRequestQueue;

    public Client() {
        this.inputThread = new Thread(() -> {
            while (acceptInput)
                sendMessage(MessageFactory.createUserTextMessage(connection.name, readInput()));
        });
        registerMessageHandlers();
    }

    public void connect(InetSocketAddress address) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(address);

        selector = Selector.open();
        this.modeChangeRequestQueue = new ModeChangeRequestQueue(selector);

        connection = new NioSocketConnection(selector, socketChannel, modeChangeRequestQueue);
        connection.messageBuffer = new ChatMessageBuffer();
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
            modeChangeRequestQueue.process();

            try {
                selector.select();
            } catch (IOException e) {
                running = false;
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isValid()) {
                    continue;
                }
                if (key.isReadable()) {
                    readFromChannel();
                } else if (key.isWritable()) {
                    writeToChannel();
                }
            }
        }
        displayText("Disconnected from server. Input anything to exit.");
        stopToAcceptInput();
    }

    private void stopToAcceptInput() {
        acceptInput = false;
        while (inputThread.isAlive()) {
            try {
                inputThread.join();
            } catch (InterruptedException e) {
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
