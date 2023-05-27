package bg.sofia.uni.fmi.mjt.dungeons;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Minion;

import java.nio.channels.Selector;

public interface ConnectorAPI {

    /**
     * selector setter
     *
     * @param selector selector new value
     */
    void setSelector(Selector selector);

    /**
     * triggers the selector to wakeup
     */
    void trigger();

    /**
     * causer setter
     *
     * @param minion attacking minion
     */
    void setCauser(Minion minion);

    /**
     * causer getter
     *
     * @return minion caused selector's wakeup
     */
    Minion getCauser();
}
