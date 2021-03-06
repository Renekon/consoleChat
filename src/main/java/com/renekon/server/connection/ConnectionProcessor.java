package com.renekon.server.connection;

import com.renekon.server.command.Command;
import com.renekon.server.command.CommandFactory;
import com.renekon.server.connection.event.ConnectionEvent;
import com.renekon.server.connection.event.DataReceivedEvent;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ConnectionProcessor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ConnectionProcessor.class.getName());
    private static final int HISTORY_SIZE = 100;

    private final ArrayBlockingQueue<ConnectionEvent> connectionEvents;
    private final ConcurrentHashMap<String, Connection> knownConnections;

    private final MessageHandlerFactory messageHandlers = new MessageHandlerFactory();
    private final ConcurrentLinkedQueue<Message> messageHistory;

    public ConnectionProcessor(ArrayBlockingQueue<ConnectionEvent> connectionEvents,
                               ConcurrentHashMap<String, Connection> knownConnections) {
        this.connectionEvents = connectionEvents;
        this.knownConnections = knownConnections;
        messageHistory = new ConcurrentLinkedQueue<>();
        registerMessageHandlers();
    }

    private void registerMessageHandlers() {
        messageHandlers.register(MessageType.USER_TEXT, userTextHandler());
        messageHandlers.register(MessageType.NAME_SENT, nameSentHandler());
    }

    private MessageHandler nameSentHandler() {
        return (message, connection) -> {
            String name = message.getAuthor();
            if (connection.name != null)
                changeName(name, connection);
            else
                registerNewConnection(name, connection);
        };
    }

    private MessageHandler userTextHandler() {
        return (message, connection) -> {
            Command command = CommandFactory.fromString((message.getText()));
            if (command != null) {
                command.execute(connection, knownConnections.values());
            } else if (!message.getText().isEmpty()) {
                saveMessageInHistory(message);
                shareMessage(message);
            }
        };
    }

    private void saveMessageInHistory(Message message) {
        synchronized (messageHistory) {
            if (messageHistory.size() > HISTORY_SIZE)
                messageHistory.poll();
            messageHistory.add(message);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConnectionEvent connectionEvent = connectionEvents.take();
                if (ConnectionEvent.Type.DATA.equals(connectionEvent.type))
                    handleMessages(connectionEvent.connection, ((DataReceivedEvent) connectionEvent).messages);
                else if (ConnectionEvent.Type.CLOSE.equals(connectionEvent.type))
                    disconnectUser(connectionEvent.connection);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted while taking ConnectionEvent from queue");
            }
        }
    }

    private void handleMessages(Connection connection, List<Message> messages) {
        for (Message message : messages) {
            MessageHandler messageHandler = messageHandlers.get(message.getType());
            if (messageHandler != null) {
                messageHandler.execute(message, connection);
            }
        }
    }

    private void disconnectUser(Connection connection) {
        if (connection.name != null && knownConnections.containsKey(connection.name)) {
            knownConnections.remove(connection.name);
            shareMessage(Connection.messageFactory.createServerTextMessage(connection.name + " left the chat."));
            LOGGER.info(String.format("User %s disconnected, %d left", connection.name, knownConnections.size()));
        }
    }


    private void shareMessage(Message message) {
        for (Connection connection : knownConnections.values()) {
            connection.write(message);
        }
    }

    private void registerNewConnection(String name, Connection connection) {
        if (checkIfNameIsValid(name, connection)) {
            sendNameAcceptedMessage(connection, name);
            shareMessage(Connection.messageFactory.createServerTextMessage(name + " joined the chat!"));
            LOGGER.info(String.format("User %d is registered as %s", knownConnections.size(), name));
            sendServerMessage(connection, "Welcome to the chat! Type /help to see list of commands");
            connection.name = name;
            knownConnections.put(name, connection);
            sendChatHistory(connection);
        }
    }

    private boolean checkIfNameIsValid(String name, Connection connection) {
        boolean valid = true;
        if (name.replaceAll("\\s", "").isEmpty()) {
            sendServerMessage(connection, "The name must be non-empty");
            valid = false;
        } else if (knownConnections.containsKey(name)) {
            sendServerMessage(connection, "The name is already used");
            valid = false;
        }
        if (!valid && connection.name == null)
            sendNameRequest(connection);
        return valid;
    }

    private void sendChatHistory(Connection connection) {
        synchronized (messageHistory) {
            Iterator<Message> iterator = messageHistory.iterator();
            for (int i = 0; i < HISTORY_SIZE && iterator.hasNext(); i++) {
                connection.write(iterator.next());
            }
        }
    }

    private void sendNameRequest(Connection connection) {
        connection.write(Connection.messageFactory.createNameRequestMessage());
    }

    private void sendServerMessage(Connection connection, String text) {
        connection.write(Connection.messageFactory.createServerTextMessage(text));
    }

    private void sendNameAcceptedMessage(Connection connection, String name) {
        connection.write(Connection.messageFactory.createNameAcceptedMessage(name));
    }

    private void changeName(String newName, Connection connection) {
        if (checkIfNameIsValid(newName, connection)) {
            sendNameAcceptedMessage(connection, newName);
            shareMessage(Connection.messageFactory.createServerTextMessage(String.format("%s is now %s.", connection.name, newName)));
            LOGGER.info(String.format("User %s is renamed to %s", connection.name, newName));
            connection.name = newName;
        }
    }
}
