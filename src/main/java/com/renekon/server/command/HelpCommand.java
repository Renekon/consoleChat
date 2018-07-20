package com.renekon.server.command;

import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public class HelpCommand implements Command {

    private final Pattern PATTERN = Pattern.compile("/help");
    private static final String DESCRIPTION = "/help --- show help";
    private final static String[] lines = {
            "Available commands:",
            HelpCommand.DESCRIPTION,
            ChangeNameCommand.DESCRIPTION,
            UsersListCommand.DESCRIPTION,
            QuitCommand.DESCRIPTION
    };

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {

        String message = String.join("\n", lines);
        connection.write(Connection.messageFactory.createServerTextMessage(message));
    }
}
