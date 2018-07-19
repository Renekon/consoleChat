package com.renekon.server.command;

import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public class UsersListCommand implements Command {

    private final Pattern PATTERN = Pattern.compile("/users");
    static final String DESCRIPTION = "/users --- show connected users";

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        StringBuilder sb;
        sb = new StringBuilder(String.format("Currently %d users connected:", knownConnections.size()));
        for (Connection c : knownConnections) {
            sb.append('\n');
            sb.append(c.name);
        }
        connection.write(connection.messageFactory.createServerTextMessage(sb.toString()));
    }
}
