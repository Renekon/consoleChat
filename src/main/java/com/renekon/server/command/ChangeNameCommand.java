package com.renekon.server.command;

import com.renekon.shared.message.MessageFactory;
import com.renekon.shared.connection.Connection;

import java.util.Collection;
import java.util.regex.Pattern;

public class ChangeNameCommand implements Command {

    static final Pattern PATTERN = Pattern.compile("/changename\\s*(.*)");
    static final String DESCRIPTION = "/changename name --- change your shat nickname";

    private String name;

    ChangeNameCommand(String name) {
        this.name = name;
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    String getName() {
        return name;
    }

    @Override
    public void execute(Connection connection, Collection<Connection> knownConnections) {
        connection.write(MessageFactory.createNameSentMessage(name).getBytes());
    }
}
