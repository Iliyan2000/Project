package bg.sofia.uni.fmi.mjt.dungeons.client;

public enum Label {
    EXCEPTION_LOG_FILE_NAME("client_exception_log.txt"),
    IO_EXCEPTION_CLIENT("Unable to connect to the server. " +
            "Try again later or contact administrator by providing the logs in " + EXCEPTION_LOG_FILE_NAME.get()),
    CONNECTION("Connected to the server."),
    CONNECTION_LOST("Connection lost."),
    DISCONNECTION("Disconnected from the server"),
    CONSOLE_INPUT_SYMBOL("> "),
    QUIT("quit"),
    HOST("localhost");

    private String message;

    Label(String message) {
        this.message = message;
    }
    public String get() {
        return message;
    }
}
