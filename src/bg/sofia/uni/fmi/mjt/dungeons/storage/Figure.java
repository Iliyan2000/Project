package bg.sofia.uni.fmi.mjt.dungeons.storage;

public enum Figure {
    OBSTACLE('#'),
    TREASURE('T'),
    MINION('M'),
    FREE_SPACE('.'),
    MULTI_ENTITY('!');

    private char figure;

    private Figure(char figure) {
        this.figure = figure;
    }
    public char get() {
        return figure;
    }
}
