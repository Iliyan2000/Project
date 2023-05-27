package bg.sofia.uni.fmi.mjt.dungeons.storage.field;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FieldAPI {

    /**
     * @return String representation of the field
     */
    String visualize();

    /**
     * creates and add new player to the field
     *
     * @param ID the ID of the new player
     */
    void addPlayer(String ID);

    /**
     * removes player with the certain ID from the field
     *
     * @param ID the ID of the player to be removed
     */
    void removePlayer(String ID);

    /**
     * provides the stats of the player with certain ID
     *
     * @param ID the ID of the player
     * @return the stats of the player
     */
    Stats getStats(String ID);

    /**
     * provides the experience of the player with certain ID
     *
     * @param ID the ID of the player
     * @return the experience of the player
     */
    int getExperience(String ID);

    /**
     * provides items in the backpack of the player with certain ID
     *
     * @param ID the ID of the player
     * @return list of treasures, that player owns
     */
    List<Treasure> getInventory(String ID);

    /**
     * provides the player to use item
     *
     * @param ID of the selected player
     * @param itemNumber of the selected item
     * @return true if the item is used successfully
     */
    boolean useItem(String ID, int itemNumber);

    /**
     * provides the player to gather an item
     *
     * @param ID of the selected player
     * @return true if the gathering is successful, false otherwise
     */
    boolean gatherItem(String ID);

    /**
     * transfers an item between two players
     *
     * @param ID of the sending player
     * @param itemNumber of the item to be transferred
     * @param receiverDisplayNumber displayNumber of the receiver
     * @return the ID of the receiver, null if there is no receiver
     */
    String sendItem(String ID, int itemNumber, int receiverDisplayNumber);

    /**
     * provides player to attack on same position
     *
     * @param ID of the attacking player
     * @return collection of the victims
     */
    Collection<Player> playerAttack(String ID);

    /**
     * provides minion to attack player on same position
     *
     * @return collection of victims
     */
    Collection<Player> minionAttack();

    /**
     * moves the player in the given direction, if possible
     *
     * @param ID of moving player
     * @param direction in which to move the player
     * @return true if teh move is successful and there is no obstacle on the path,
     * false otherwise
     */
    boolean move(String ID, Direction direction);

    /**
     * teleport the player to a free random place on the field
     *
     * @param ID of the respawning player
     */
    void respawn(String ID);

    /**
     * connector getter
     *
     * @return the connector
     */
    Connector connector();

    /**
     * obstacle setter
     *
     * @param obstacles new value
     */
    void setObstacles(Set<Coordinates> obstacles);

    /**
     * treasure setter
     *
     * @param treasures new value
     */
    void setTreasures(Map<Coordinates, Treasure> treasures);

    /**
     * minion setter
     *
     * @param minions new value
     */
    void setMinions(Map<Coordinates, Minion> minions);

    /**
     * players setter
     *
     * @param players new value
     */
    void setPlayers(Map<Player, Coordinates> players);

    /**
     * search for player with certain ID
     *
     * @param ID ID of the player
     * @return player
     */
    Player getPlayer(String ID);
}
