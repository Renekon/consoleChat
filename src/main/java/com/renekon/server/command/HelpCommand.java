package com.renekon.server.command;

import com.renekon.shared.message.MessageFactory;
import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public class HelpCommand implements Command {

    private final Pattern PATTERN = Pattern.compile("/help");
    private static final String DESCRIPTION = "/help --- show help";

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        String[] lines = {
                "Available commands:",
                HelpCommand.DESCRIPTION,
                ChangeNameCommand.DESCRIPTION,
                UsersListCommand.DESCRIPTION,
                QuitCommand.DESCRIPTION
        };
        String message = String.join("\n", lines);
        connection.write(MessageFactory.createServerTextMessage(message).getBytes());
    }
}
