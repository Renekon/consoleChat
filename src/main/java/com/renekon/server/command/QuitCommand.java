package com.renekon.server.command;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.MessageFactory;

import java.util.Collection;
import java.util.regex.Pattern;

public class QuitCommand implements Command {

    private final Pattern PATTERN = Pattern.compile("\\s*\\\\quit\\s*$");
    public static final String DESCRIPTION = "\\quit --- quit the chat";

    public QuitCommand() {
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.write(MessageFactory.createDisconnectMessage().getBytes());
        connection.requestClose();
    }
}
