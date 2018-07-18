package com.renekon.server.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class CommandTest {
    @Test
    void fromString() {
        Command command = CommandFactory.fromString("  \\help   ");
        Assertions.assertTrue(HelpCommand.class.isInstance(command));

        command = CommandFactory.fromString(" \\name  new name  ");
        Assertions.assertTrue(ChangeNameCommand.class.isInstance(command));
        Assertions.assertEquals("new name  ", ((ChangeNameCommand) command).getName());

        command = CommandFactory.fromString("  \\list    ");
        Assertions.assertTrue(UsersListCommand.class.isInstance(command));
    }
}
