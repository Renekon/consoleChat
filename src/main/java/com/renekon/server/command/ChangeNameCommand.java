package com.renekon.server.command;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.MessageFactory;

import java.util.Collection;
import java.util.regex.Pattern;

public class ChangeNameCommand implements Command {

    public static final Pattern PATTERN = Pattern.compile("\\s*\\\\changename\\s*(.*)$");
    public static final String DESCRIPTION = "\\changename name --- change the name to a new one";

    private String name;

    public ChangeNameCommand(String name) {
        this.name = name;
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    public String getName() {
        return name;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.write(MessageFactory.createNameSentMessage(name).getBytes());
    }
}
