package com.renekon.server.command;

import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public class QuitCommand implements Command {

    private final Pattern PATTERN = Pattern.compile("/quit");
    static final String DESCRIPTION = "/quit --- quit the chat";

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.requestClose();
        connection.write(connection.messageFactory.createServerTextMessage("Good bye!"));
        connection.write(connection.messageFactory.createDisconnectMessage());
    }
}
