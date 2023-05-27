package bg.sofia.uni.fmi.mjt.dungeons;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Minion;

import java.nio.channels.Selector;

public class Connector implements ConnectorAPI {
    private Selector selector;
    private Minion causer;

    public Connector() {
        selector = null;
        causer = null;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public synchronized void trigger() {
        selector.wakeup();
    }

    public synchronized void setCauser(Minion minion) {
        causer = minion;
    }

    public Minion getCauser() {
        Minion minion = causer;
        causer = null;
        return minion;
    }
}
