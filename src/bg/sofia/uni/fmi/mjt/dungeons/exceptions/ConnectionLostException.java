package bg.sofia.uni.fmi.mjt.dungeons.exceptions;

public class ConnectionLostException extends RuntimeException {
    public ConnectionLostException(String message) {
        super(message);
    }
}
