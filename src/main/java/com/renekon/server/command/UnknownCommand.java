package com.renekon.server.command;

import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

class UnknownCommand implements Command {

    @Override
    public Pattern getPattern() {
        return null;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.write(connection.messageFactory.createServerTextMessage("There is no such command. Type /help to see the list"));
    }
}
