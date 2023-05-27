package bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;

import java.util.List;

public interface PlayerAPI {

    /**
     * ID getter
     *
     * @return the ID of the Player
     */
    String getID();

    /**
     * experience getter
     *
     * @return the experience of the Player
     */
    int getExperience();

    /**
     * backpack getter
     *
     * @return list of treasures owned by the Player
     */
    List<Treasure> getBackpack();

    /**
     * uses the treasure in the backpack by the number of itemNumber
     * and remove it from the backpack
     *
     * @param itemNumber number of the selected item in the backpack
     * @return true the item was used successfully, false otherwise
     */
    boolean use(int itemNumber);

    /**
     * adds the treasure to the backpack, if is possible
     *
     * @param treasure that try to be added in the backpack
     * @return true if the treasure is added successfully
     */
    boolean gather(Treasure treasure);

    /**
     * drops the item with the selected number in the backpack
     *
     * @param itemNumber number of the selected item in the backpack
     * @return the selected treasure from the backpack,
     * null if there is no fitting item with this number
     */
    Treasure drop(int itemNumber);

    /**
     * drops random item from the backpack, if there is any
     *
     * @return random treasure from the backpack, null if it's empty
     */
    Treasure dropRandom();

    /**
     * resets the player's stats as if they are level 1
     */
    void resetStats();

    /**
     * increase the player's experience
     *
     * @param experience amount of experience to be added to player's one
     */
    void takeExperience(int experience);
}
