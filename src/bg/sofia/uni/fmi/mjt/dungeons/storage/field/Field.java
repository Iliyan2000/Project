package bg.sofia.uni.fmi.mjt.dungeons.storage.field;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;
import bg.sofia.uni.fmi.mjt.dungeons.exceptions.AbsentPlayerException;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Figure;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Weapon;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.spell.HealthPotion;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.spell.ManaPotion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class Field implements FieldAPI {
    static final char nullChar = '\0';
    static final String NO_PLAYER_FOUND_MESSAGE = "No player found with such ID";

    private static final int MAX_LEVEL = 10;
    private static final int BASE_EXPERIENCE = MAX_LEVEL;

    private final int mapLimit;
    private final Connector connector;

    private Set<Coordinates> obstacles;
    private Map<Coordinates, Treasure> treasures;
    private Map<Coordinates, Minion> minions;
    private Map<Player, Coordinates> players;

    public Field(int mapLimit) {
        this.mapLimit = mapLimit;
        int obstacleCount = (int) Math.round(mapLimit * mapLimit * 0.4);

        connector = new Connector();

        obstacles = new HashSet<>();
        treasures = new HashMap<>();
        minions = new ConcurrentHashMap<>();
        players = new TreeMap<>();

        for (int i = 0; i < obstacleCount; i++) {
            obstacles.add(getFreeCoordinates());
        }
        for (int i = 1; i < mapLimit; i += 2) {
            treasures.put(getFreeCoordinates(), new Weapon(i % MAX_LEVEL + 1));
        }
        boolean health = true;
        for (int i = 1; i < mapLimit; i += 2) {
            if (health) {
                treasures.put(getFreeCoordinates(), new HealthPotion(i % MAX_LEVEL + 1));
                health = false;
            } else {
                treasures.put(getFreeCoordinates(), new ManaPotion(i % MAX_LEVEL + 1));
                health = true;
            }
        }
        for (int i = 1; i <= mapLimit; i++) {
            minions.put(getFreeCoordinates(), new Minion(i % MAX_LEVEL + 1, connector));
        }
    }

    public String visualize() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < mapLimit; i++) {
            for (int j = 0; j < mapLimit; j++) {
                result.append(getFigure(new Coordinates(i, j)));
            }
            result.append(System.lineSeparator());
        }

        return result.toString();
    }

    public void addPlayer(String ID) {
        OptionalInt optional = players.keySet().stream().mapToInt(Player::getDisplayNumber).max();

        int nextPlayerNumber = optional.isPresent() ? optional.getAsInt() : 0;
        players.put(new Player(ID, nextPlayerNumber + 1), getFreeCoordinates());
    }

    public void removePlayer(String ID) {
        players.remove(getPlayer(ID));
    }

    public Stats getStats(String ID) {
        return getPlayer(ID).stats();
    }

    public int getExperience(String ID) {
        return getPlayer(ID).getExperience();
    }

    public List<Treasure> getInventory(String ID) {
        return getPlayer(ID).getBackpack();
    }

    public boolean useItem(String ID, int itemNumber) {
        return getPlayer(ID).use(itemNumber);
    }

    public boolean gatherItem(String ID) {
        Player player = getPlayer(ID);
        Coordinates playerCoordinates = players.get(player);

        if (player.gather(treasures.get(playerCoordinates))) {
            treasures.remove(playerCoordinates);
            return true;
        }

        return false;
    }

    public String sendItem(String ID, int itemNumber, int receiverDisplayNumber) {
        Player receiver = getPlayerByDisplayNumber(receiverDisplayNumber);
        Player sender = getPlayer(ID);
        Treasure item = sender.drop(itemNumber);
        if (players.get(sender).equals(players.get(receiver)) &&
                receiver.gather(item)) {
            return receiver.getID();
        }
        sender.gather(item);
        return null;
    }

    public Collection<Player> playerAttack(String ID) {
        Player causer = getPlayer(ID);

        attackMinions(causer);
        return attackVictims(causer);
    }

    public Collection<Player> minionAttack() {
        Minion attackingMinion = connector.getCauser();
        if (attackingMinion == null) {
            return null;
        }

        Collection<Player> victims = getPlayerByCoordinates(minionCoordinates(attackingMinion));

        for (Player player : victims) {
            player.takeDamage(attackingMinion.attack());
            if (player.isDead()) {
                onKill(player);
            }
        }

        return victims;
    }

    public boolean move(String ID, Direction direction) {
        Player player = getPlayer(ID);
        Coordinates lastCoordinates = players.get(player);

        Coordinates newCoordinates = switch (direction) {
            case up -> up(lastCoordinates);
            case left -> left(lastCoordinates);
            case right -> right(lastCoordinates);
            case down -> down(lastCoordinates);
        };
        if (checkCoordinatesUsageMove(newCoordinates)) {
            return false;
        }

        players.replace(player, newCoordinates);
        return true;
    }

    public void respawn(String ID) {
        players.replace(getPlayer(ID), getFreeCoordinates());
    }

    public Connector connector() {
        return connector;
    }

    public void setObstacles(Set<Coordinates> obstacles) {
        this.obstacles = obstacles;
    }
    public void setTreasures(Map<Coordinates, Treasure> treasures) {
        this.treasures = treasures;
    }
    public void setMinions(Map<Coordinates, Minion> minions) {
        this.minions = minions;
    }
    public void setPlayers(Map<Player, Coordinates> players) {
        this.players = players;
    }

    public Collection<Minion> getMinions() { return minions.values(); }

    public Player getPlayer(String ID) {
        for (Player player : players.keySet()) {
            if (player.getID().equals(ID)) {
                return player;
            }
        }
        throw new AbsentPlayerException(NO_PLAYER_FOUND_MESSAGE);
    }

    private Coordinates getFreeCoordinates() {
        Random rand = new Random();
        Coordinates result = new Coordinates(rand.nextInt(mapLimit), rand.nextInt(mapLimit));
        while (checkCoordinatesUsageSpawn(result)) {
            result = new Coordinates(rand.nextInt(mapLimit), rand.nextInt(mapLimit));
        }
        return result;
    }

    private boolean checkCoordinatesUsageSpawn(Coordinates coordinates) {
        return obstacles.contains(coordinates) ||
                treasures.containsKey(coordinates) ||
                minions.containsKey(coordinates) ||
                players.containsValue(coordinates);
    }

    private boolean checkCoordinatesUsageMove(Coordinates coordinates) {
        return obstacles.contains(coordinates) ||
                coordinates.x() < 0 || coordinates.x() > mapLimit - 1 ||
                coordinates.y() < 0 || coordinates.y() > mapLimit - 1;
    }

    private char getFigure(Coordinates coordinates) {
        if (obstacles.contains(coordinates)) {
            return Figure.OBSTACLE.get();
        }

        char playerFigure = nullChar;
        if (players.containsValue(coordinates)) {
            playerFigure = getPlayerFigure(coordinates);
        }

        if (treasures.containsKey(coordinates)) {
            return playerFigure != nullChar ?
                    Figure.MULTI_ENTITY.get() :
                    Figure.TREASURE.get();
        }
        if (minions.containsKey(coordinates)) {
            return playerFigure != nullChar ?
                    Figure.MULTI_ENTITY.get() :
                    Figure.MINION.get();
        }

        return playerFigure != nullChar ?
                playerFigure :
                Figure.FREE_SPACE.get();
    }

    private char getPlayerFigure(Coordinates coordinates) {
        int playersNumber = -1;

        for (Map.Entry<Player, Coordinates> player : players.entrySet()) {
            if (player.getValue().equals(coordinates)) {
                if (playersNumber != -1) {
                    return Figure.MULTI_ENTITY.get();
                }
                playersNumber = player.getKey().getDisplayNumber();
            }
        }

        return (char) (playersNumber + '0');
    }

    private Player getPlayerByDisplayNumber(int displayNumber) {
        for (Player player : players.keySet()) {
            if (player.getDisplayNumber() == displayNumber) {
                return player;
            }
        }
        throw new AbsentPlayerException(NO_PLAYER_FOUND_MESSAGE);
    }

    private Collection<Player> getPlayerByCoordinates(Coordinates coordinates) {
        Collection<Player> result = new HashSet<>();

        for (Map.Entry<Player, Coordinates> player : players.entrySet()) {
            if (player.getValue().equals(coordinates)) {
                result.add(player.getKey());
            }
        }

        return result;
    }

    private Collection<Player> attackVictims(Player causer) {
        Collection<Player> result = new HashSet<>();
        Coordinates coordinates = players.get(causer);

        for (Player player : getPlayerByCoordinates(coordinates)) {

            if (player != causer) {
                player.takeDamage(causer.attack());
                result.add(player);

                if (player.isDead()) {
                    onKill(player);
                }
            }
        }

        return result;
    }

    private void attackMinions(Player causer) {
        Coordinates coordinates = players.get(causer);

        for (Map.Entry<Coordinates, Minion> minion : minions.entrySet()) {
            if (minion.getKey().equals(coordinates)) {
                Minion victim = minion.getValue();
                if (!victim.isAlive()) {
                    victim.start();
                }
                victim.takeDamage(causer.attack());
                if (victim.isDead()) {
                    victim.interrupt();
                    int minionLevel = victim.level();
                    minions.remove(coordinates);
                    minions.put(getFreeCoordinates(), new Minion(minionLevel, connector));
                    causer.takeExperience(experience(minionLevel));
                }
            }
        }
    }

    private int experience(int level) {
        return level * BASE_EXPERIENCE;
    }

    private Coordinates minionCoordinates(Minion minion) {

        for (Map.Entry<Coordinates, Minion> minionEntry : minions.entrySet()) {
            if (minionEntry.getValue().equals(minion)) {
                return minionEntry.getKey();
            }
        }

        return null;
    }

    private void onKill(Player player) {
        respawn(player.getID());
        player.resetStats();
        Treasure droppedItem = player.dropRandom();

        if (droppedItem != null) {
            Coordinates oldCoordinates = players.get(player);
            Coordinates newCoordinates = oldCoordinates;

            if (treasures.containsKey(oldCoordinates)) {
                newCoordinates = getFreeCoordinates();
            }

            treasures.put(newCoordinates, droppedItem);
        }
    }

    private Coordinates up(Coordinates coordinates) {
        return new Coordinates(coordinates.x() - 1, coordinates.y());
    }

    private Coordinates left(Coordinates coordinates) {
        return new Coordinates(coordinates.x(), coordinates.y() - 1);
    }

    private Coordinates down(Coordinates coordinates) {
        return new Coordinates(coordinates.x() + 1, coordinates.y());
    }

    private Coordinates right(Coordinates coordinates) {
        return new Coordinates(coordinates.x(), coordinates.y() + 1);
    }
}
