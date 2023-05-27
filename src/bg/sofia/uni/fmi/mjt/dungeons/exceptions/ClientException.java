package bg.sofia.uni.fmi.mjt.dungeons.exceptions;

public class ClientException extends RuntimeException {
    public ClientException(String message, Throwable e) {
        super(message, e);
    }
}
