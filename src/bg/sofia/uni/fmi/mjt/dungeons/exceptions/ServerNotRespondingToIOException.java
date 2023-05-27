package bg.sofia.uni.fmi.mjt.dungeons.exceptions;

public class ServerNotRespondingToIOException extends RuntimeException {
    public ServerNotRespondingToIOException(String message, Throwable e) {
        super(message,e);
    }
}
