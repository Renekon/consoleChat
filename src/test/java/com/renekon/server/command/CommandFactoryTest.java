package com.renekon.server.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class CommandFactoryTest {
    @Test
    void fromString() {
        Command command = CommandFactory.fromString("/help");
        Assertions.assertTrue(HelpCommand.class.isInstance(command));

        command = CommandFactory.fromString("/changename  new name");
        Assertions.assertTrue(ChangeNameCommand.class.isInstance(command));
        Assertions.assertEquals("new name", ((ChangeNameCommand) command).getName());

        command = CommandFactory.fromString("/users");
        Assertions.assertTrue(UsersListCommand.class.isInstance(command));

        command = CommandFactory.fromString("/quit");
        Assertions.assertTrue(QuitCommand.class.isInstance(command));

        command = CommandFactory.fromString("/somecommand sdfsd  ");
        Assertions.assertTrue(UnknownCommand.class.isInstance(command));
    }
}
