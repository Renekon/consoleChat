package com.renekon.server.command;

import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public interface Command {

    Pattern COMMAND_PATTERN = Pattern.compile("/.*");

    Pattern getPattern();

    void execute(Connection connection, Collection<Connection> knownConnections);

}
