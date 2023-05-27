package bg.sofia.uni.fmi.mjt.dungeons.server;

import bg.sofia.uni.fmi.mjt.dungeons.command.commandexecutor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.dungeons.storage.field.Field;

public class ServerStart {

    private static final int MAP_LIMIT = 10;
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT, new CommandExecutor(new Field(MAP_LIMIT)));
        server.start();
    }
}