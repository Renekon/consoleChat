package com.renekon.server.connection;

import com.renekon.server.command.Command;
import com.renekon.server.command.CommandFactory;
import com.renekon.shared.connection.Connection;
import com.renekon.shared.connection.event.ConnectionEvent;
import com.renekon.shared.connection.event.DataReceivedEvent;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageType;
import com.renekon.shared.message.handler.MessageHandler;
import com.renekon.shared.message.handler.MessageHandlerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
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
                synchronized (messageHistory) {
                    if (messageHistory.size() > HISTORY_SIZE)
                        messageHistory.poll();
                    messageHistory.add(message);
                }
                shareMessage(message);
            }
        };
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConnectionEvent connectionEvent = connectionEvents.take();
                if (ConnectionEvent.Type.DATA.equals(connectionEvent.type))
                    handleData(connectionEvent.connection, ((DataReceivedEvent) connectionEvent).data);
                else if (ConnectionEvent.Type.CLOSE.equals(connectionEvent.type))
                    handleClose(connectionEvent.connection);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while taking ConnectionEvent from queue", e);
            }
        }
    }

    private void handleData(Connection connection, List<Message> messages) {
        for (Message message : messages) {
            MessageHandler messageHandler = messageHandlers.get(message.getType());
            if (messageHandler != null) {
                messageHandler.execute(message, connection);
            }
        }
    }

    private void handleClose(Connection connection) {
        if (connection.name != null && knownConnections.containsKey(connection.name)) {
            knownConnections.remove(connection.name);
            shareMessage(connection.messageFactory.createServerTextMessage(connection.name + " left the chat."));
            LOGGER.info(String.format("User %s disconnected, %d left", connection.name, knownConnections.size()));
        }
    }


    private void shareMessage(Message message) {
        for (Connection connection : knownConnections.values()) {
            connection.write(message);
        }
    }

    private void registerNewConnection(String name, Connection connection) {
        if (name.isEmpty()) {
            sendServerMessage(connection, "The name must be non-empty");
            sendNameRequest(connection);
        } else if (knownConnections.containsKey(name)) {
            sendServerMessage(connection, "The name is already used");
            sendNameRequest(connection);
        } else {
            sendNameAcceptedMessage(connection, name);
            connection.name = name;
            sendServerMessage(connection, "Welcome to the chat! Type \\help for help.");
            knownConnections.put(name, connection);
            shareMessage(connection.messageFactory.createServerTextMessage(name + " joined the chat!"));
            synchronized (messageHistory) {
                Iterator<Message> iterator = messageHistory.iterator();
                for (int i = 0; i < HISTORY_SIZE && iterator.hasNext(); i++) {
                    connection.write(iterator.next());
                }

            }
            LOGGER.info(String.format("User %d is registered as %s", knownConnections.size(), name));
        }
    }

    private void sendNameRequest(Connection connection) {
        connection.write(connection.messageFactory.createNameRequestMessage());
    }

    private void sendServerMessage(Connection connection, String text) {
        connection.write(connection.messageFactory.createServerTextMessage(text));
    }

    private void sendNameAcceptedMessage(Connection connection, String name) {
        connection.write(connection.messageFactory.createNameAcceptedMessage(name));
    }

    private void changeName(String newName, Connection connection) {
        if (newName.isEmpty()) {
            sendServerMessage(connection, "The name must be non-empty");
        } else if (knownConnections.containsKey(newName)) {
            sendServerMessage(connection, "The name is already used");
        } else {
            String oldName = connection.name;
            knownConnections.remove(connection.name);
            connection.name = newName;
            knownConnections.put(connection.name, connection);
            sendNameAcceptedMessage(connection, newName);
            shareMessage(connection.messageFactory.createServerTextMessage(String.format("%s is now %s.", oldName, newName)));
            LOGGER.info(String.format("User %s is renamed to %s", oldName, newName));
        }
    }
}
