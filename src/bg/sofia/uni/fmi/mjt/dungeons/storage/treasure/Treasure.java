package bg.sofia.uni.fmi.mjt.dungeons.storage.treasure;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;

public interface Treasure {

    /**
     * assigns the stats to its owner
     *
     * @param ownerStats the stats of the owner
     */
    void assign(Stats ownerStats);

    /**
     * the item's effect is performed
     *
     * @return true if the item is used properly, false otherwise
     */
    boolean use();

    /**
     * level getter
     *
     * @return the level of the treasure
     */
    int level();

    /**
     * @return the information of the treasure formatted in String
     */
    String mainInformation();
}
