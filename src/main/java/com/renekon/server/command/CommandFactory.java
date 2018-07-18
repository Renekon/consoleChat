package com.renekon.server.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class CommandFactory {

    private static Command unknown = new UnknownCommand();

    private static final List<Command> staticKnownCommands = Arrays.asList(
            new HelpCommand(),
            new UsersListCommand(),
            new QuitCommand()
    );



    public static Command fromString(String string) {
        if (!Command.COMMAND_PATTERN.matcher(string).matches()) {
            return null;
        }

        for (Command command: staticKnownCommands){
            if (command.getPattern().matcher(string).matches())
                return command;
        }
        Matcher matcher = ChangeNameCommand.PATTERN.matcher(string);
        if (matcher.matches())
            return new ChangeNameCommand(matcher.group(1));

        return unknown;
    }

}
