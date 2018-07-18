package com.renekon.server.command;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.MessageFactory;

import java.util.Collection;
import java.util.regex.Pattern;

class UnknownCommand implements Command {

    public UnknownCommand() {
    }

    @Override
    public Pattern getPattern() {
        return null;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.write(MessageFactory.createServerTextMessage("There is no such command. Type \\help to see the list").getBytes());
    }
}
