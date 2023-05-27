package bg.sofia.uni.fmi.mjt.dungeons.command;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    static final String TOO_MUCH_ARGUMENTS = "Too much arguments in the command." + System.lineSeparator();

    private static List<String> getCommandArguments(String input) {
        return Arrays.stream(input.split(" ")).map(String::strip).toList();
    }

    public static Command newCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);

        if (tokens.size() > 2) {
            throw new IllegalArgumentException(TOO_MUCH_ARGUMENTS);
        }
        try {
            return new Command(CommandType.valueOf(tokens.get(0)),
                    tokens.size() == 2 ? tokens.get(1) : null);

        } catch (IllegalArgumentException e) {
            return new Command(CommandType.unknown, null);
        }
    }
}
