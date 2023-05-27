package bg.sofia.uni.fmi.mjt.dungeons.client;

public class ClientStart {

    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        Client client = new Client(SERVER_PORT);
        client.start();
    }
}
