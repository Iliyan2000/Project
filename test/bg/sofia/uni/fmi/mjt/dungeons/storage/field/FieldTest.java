package bg.sofia.uni.fmi.mjt.dungeons.storage.field;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;
import bg.sofia.uni.fmi.mjt.dungeons.exceptions.AbsentPlayerException;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Coordinates;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Minion;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldTest {
    static final int MAP_LIMIT = 10;
    static final int BASE_LEVEL = 1;
    static final String testID = "testID";

    @Mock
    Connector connector;

    Field field;

    @BeforeEach
    void init() {
        field = new Field(MAP_LIMIT);
        field.addPlayer(testID);
    }

    @Test
    void testVisualize() {
        String testID1 = "testID1";
        int testInt1 = 1;
        int testInt2 = 2;
        Set<Coordinates> obstacles = Set.of(new Coordinates(1, 1));
        Map<Coordinates, Treasure> treasures = Map.of(new Coordinates(0, 0), new Weapon(BASE_LEVEL));
        Map<Coordinates, Minion> minions = Map.of(new Coordinates(1, 0), new Minion(BASE_LEVEL, connector),
                new Coordinates(2, 2), new Minion(BASE_LEVEL, connector));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), new Coordinates(0, 1),
                new Player(testID1, testInt2), new Coordinates(2, 2));

        field = new Field(3);
        field.setObstacles(obstacles);
        field.setTreasures(treasures);
        field.setMinions(minions);
        field.setPlayers(players);

        String expectedOutput = "T1." + System.lineSeparator() +
                "M#." + System.lineSeparator() +
                "..!" + System.lineSeparator();

        String response = field.visualize();
        assertEquals(response, expectedOutput);
    }

    @Test
    void testAddPlayer() {
        String testID1 = "testID1";
        assertThrows(AbsentPlayerException.class, () -> field.getPlayer(testID1));

        field.addPlayer(testID1);

        Player testPlayer = field.getPlayer(testID1);
        assertEquals(testPlayer.getID(), testID1);
    }

    @Test
    void testRemovePlayer() {
        Player player = field.getPlayer(testID);
        assertEquals(player.getID(), testID);

        field.removePlayer(testID);
        assertThrows(AbsentPlayerException.class, () -> field.getPlayer(testID));
    }

    @Test
    void testGetStats() {
        int baseLevel = 1;
        int baseHealth = 100;
        int baseMana = 100;
        int baseAttack = 50;
        int baseDefence = 30;

        Stats stats = field.getStats(testID);
        assertEquals(stats.level(), baseLevel);
        assertEquals(stats.health(), baseHealth);
        assertEquals(stats.mana(), baseMana);
        assertEquals(stats.attack(), baseAttack);
        assertEquals(stats.defence(), baseDefence);
    }

    @Test
    void testGetExperience() {
        int baseExperience = 0;
        assertEquals(field.getExperience(testID), baseExperience);
    }

    @Test
    void testGetInventory() {
        assertEquals(field.getInventory(testID), List.of());
    }

    @Test
    void testUseItemEmptySlot() {
        int testItemNumber = 1;
        assertFalse(field.useItem(testID, testItemNumber));
    }

    @Test
    void testUseItemNormal() {
        int testInt = 1;
        int baseAttack = 50;
        int itemBoost = 5;
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinates, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt), testCoordinates);
        field.setTreasures(treasures);
        field.setPlayers(players);
        field.gatherItem(testID);

        Stats stats = field.getStats(testID);
        assertEquals(stats.attack(), baseAttack);

        assertTrue(field.useItem(testID, testInt));

        stats = field.getStats(testID);
        assertEquals(stats.attack(), baseAttack + itemBoost);
    }

    @Test
    void testGatherItemEmptyField() {
        field.addPlayer(testID);
        field.setTreasures(new HashMap<>());

        assertFalse(field.gatherItem(testID));
    }

    @Test
    void testGatherItemNormal() {
        int testInt = 1;
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinates, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt), testCoordinates);
        field.setTreasures(treasures);
        field.setPlayers(players);

        List<Treasure> backpack = field.getInventory(testID);
        assertEquals(backpack.size(), 0);

        assertTrue(field.gatherItem(testID));

        backpack = field.getInventory(testID);
        assertEquals(backpack.size(), 1);

        assertFalse(field.gatherItem(testID));
    }

    @Test
    void testSendItemNoReceiverInGame() {
        int testInt1 = 1;
        int testInt2 = 2;
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinates, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinates);
        field.setTreasures(treasures);
        field.setPlayers(players);
        field.gatherItem(testID);

        assertThrows(AbsentPlayerException.class,
                () -> field.sendItem(testID, testInt1, testInt2));
    }

    @Test
    void testSendItemNoReceiverOnSpot() {
        int testInt1 = 1;
        int testInt2 = 2;
        String testID1 = "testID1";
        Coordinates testCoordinatesSender = new Coordinates(0, 0);
        Coordinates testCoordinatesReceiver = new Coordinates(0, 1);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinatesSender, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinatesSender,
                new Player(testID1, testInt2), testCoordinatesReceiver);
        field.setTreasures(treasures);
        field.setPlayers(players);
        field.gatherItem(testID);

        assertNull(field.sendItem(testID, testInt1, testInt2));
    }

    @Test
    void testSendItemNoItemOnSlot() {
        int testInt1 = 1;
        int testInt2 = 2;
        String testID1 = "testID1";
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinates,
                new Player(testID1, testInt2), testCoordinates);
        field.setPlayers(players);
        field.gatherItem(testID);

        assertNull(field.sendItem(testID, testInt1, testInt2));
    }

    @Test
    void testSendItemNormal() {
        int testInt1 = 1;
        int testInt2 = 2;
        String testID1 = "testID1";
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinates, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinates,
                new Player(testID1, testInt2), testCoordinates);
        field.setTreasures(treasures);
        field.setPlayers(players);
        field.gatherItem(testID);

        assertEquals(field.getInventory(testID).size(), 1);
        assertEquals(field.getInventory(testID1).size(), 0);

        assertEquals(field.sendItem(testID, testInt1, testInt2), testID1);

        assertEquals(field.getInventory(testID).size(), 0);
        assertEquals(field.getInventory(testID1).size(), 1);
    }

    @Test
    void testSendItemTwoPeopleOnSameSpot() {
        int testInt1 = 1;
        int testInt2 = 2;
        int testInt3 = 3;
        String testID1 = "testID1";
        String testID2 = "testID2";
        Coordinates testCoordinates = new Coordinates(0, 0);
        Map<Coordinates, Treasure> treasures = new HashMap<>();
        treasures.put(testCoordinates, new Weapon(BASE_LEVEL));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinates,
                new Player(testID1, testInt2), testCoordinates,
                new Player(testID2, testInt3), testCoordinates);
        field.setTreasures(treasures);
        field.setPlayers(players);
        field.gatherItem(testID);

        assertEquals(field.getInventory(testID2).size(), 0);

        assertEquals(field.sendItem(testID, testInt1, testInt2), testID1);

        assertEquals(field.getInventory(testID2).size(), 0);
    }

    @Test
    void testPlayerAttackNoEnemies() {
        field.addPlayer(testID);

        Collection<Player> victims = field.playerAttack(testID);
        assertEquals(victims.size(), 0);
    }

    @Test
    void testPlayerAttackMultipleEnemies() {
        int baseHealth = 100;
        int baseAttack = 50;
        int baseDefence = 30;
        int baseDamage = baseAttack - baseDefence;
        int testInt1 = 1;
        int testInt2 = 2;
        int testInt3 = 3;
        int testInt4 = 4;
        String testID1 = "testID1";
        String testID2 = "testID2";
        String testID3 = "testID3";
        Player testPlayer = new Player(testID1, testInt2);
        Player testPlayer1 = new Player(testID2, testInt3);
        Player testPlayer2 = new Player(testID3, testInt4);
        Coordinates testCoordinates = new Coordinates(0, 0);
        Coordinates testCoordinates1 = new Coordinates(0, 1);
        Map<Coordinates, Minion> minions = Map.of(testCoordinates, new Minion(BASE_LEVEL, connector));
        Map<Player, Coordinates> players = Map.of(new Player(testID, testInt1), testCoordinates,
                testPlayer, testCoordinates,
                testPlayer1, testCoordinates,
                testPlayer2, testCoordinates1);
        field.setMinions(minions);
        field.setPlayers(players);

        Collection<Minion> minionVictims = field.getMinions();
        Iterator<Minion> iterator = minionVictims.iterator();
        while (iterator.hasNext()) {
            assertEquals(iterator.next().stats().health(), baseHealth);
        }
        assertEquals(testPlayer.stats().health(), baseHealth);
        assertEquals(testPlayer1.stats().health(), baseHealth);
        assertEquals(testPlayer2.stats().health(), baseHealth);

        Collection<Player> playerVictims = field.playerAttack(testID);
        assertTrue(playerVictims.contains(testPlayer));
        assertTrue(playerVictims.contains(testPlayer1));
        assertFalse(playerVictims.contains(testPlayer2));

        iterator = minionVictims.iterator();
        while (iterator.hasNext()) {
            assertEquals(iterator.next().stats().health(), baseHealth - baseDamage);
        }
        assertEquals(testPlayer.stats().health(), baseHealth - baseDamage);
        assertEquals(testPlayer1.stats().health(), baseHealth - baseDamage);
        assertEquals(testPlayer2.stats().health(), baseHealth);
    }

    @Test
    void testMinionAttack() {
    }

    @Test
    void move() {
    }

    @Test
    void respawn() {
    }
}