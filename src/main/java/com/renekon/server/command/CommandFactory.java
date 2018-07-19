package com.renekon.server.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {

    private static Command defaultCommand = new UnknownCommand();

    private static final List<Command> commonCommands = Arrays.asList(
            new HelpCommand(),
            new UsersListCommand(),
            new QuitCommand()
    );

    public static Command fromString(String string) {
        if (!stringMatchesPattern(string, Command.COMMAND_PATTERN)) {
            return null;
        }

        for (Command command : commonCommands) {
            if (stringMatchesPattern(string, command.getPattern()))
                return command;
        }
        Matcher matcher = ChangeNameCommand.PATTERN.matcher(string);
        if (matcher.matches())
            return new ChangeNameCommand(matcher.group(1));

        return defaultCommand;
    }

    private static boolean stringMatchesPattern(String string, Pattern pattern) {
        return pattern.matcher(string).matches();
    }
}
