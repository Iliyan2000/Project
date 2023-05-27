package bg.sofia.uni.fmi.mjt.dungeons.command.commandexecutor;

import bg.sofia.uni.fmi.mjt.dungeons.command.Command;
import bg.sofia.uni.fmi.mjt.dungeons.command.CommandType;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.Message;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.ReceiversAnswer;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.SenderReceivers;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.storage.field.Field;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Weapon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {

    static final String testID = "testID";
    static final int BASE_LEVEL = 1;

    @Mock
    Field field;

    @InjectMocks
    CommandExecutor executor;

    @Test
    void testExecuteHelp() {
        Command testCommand = new Command(CommandType.help, null);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.HELP.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteStats() {
        Command testCommand = new Command(CommandType.stats, null);
        when(field.getStats(testID)).thenReturn(new Stats(BASE_LEVEL));
        when(field.getExperience(testID)).thenReturn(0);
        String expectedResponse = """
                level:      1
                experience: 0/100
                health:     100
                mana:       100
                attack:     50
                defence:    30"""
                + System.lineSeparator();

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), expectedResponse);
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteInventoryEmpty() {
        Command testCommand = new Command(CommandType.inventory, null);
        when(field.getInventory(testID)).thenReturn(List.of());
        int inventoryLimit = 10;
        StringBuilder expectedResponse = new StringBuilder();
        for (int i = 0; i < inventoryLimit; i++) {
            expectedResponse.append(i + 1).append('.').append(System.lineSeparator());
        }

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), expectedResponse.toString());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteInventoryOneItem() {
        Command testCommand = new Command(CommandType.inventory, null);
        when(field.getInventory(testID)).thenReturn(List.of(new Weapon(BASE_LEVEL)));
        int inventoryLimit = 10;
        String weaponString = """
                Weapon:
                Level: 1
                ManaCost: 0""" + System.lineSeparator();
        StringBuilder expectedResponse = new StringBuilder("1." +
                System.lineSeparator() +
                weaponString);
        for (int i = 1; i < inventoryLimit; i++) {
            expectedResponse.append(i + 1).append('.').append(System.lineSeparator());
        }

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), expectedResponse.toString());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteItemNormal() {
        String testArgs = "1";
        Command testCommand = new Command(CommandType.item, testArgs);
        when(field.useItem(testID, Integer.parseInt(testArgs))).thenReturn(true);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.ITEM_USED.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteItemNoItemOnSlot() {
        String testArgs = "1";
        Command testCommand = new Command(CommandType.item, testArgs);
        when(field.useItem(testID, Integer.parseInt(testArgs))).thenReturn(false);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.ITEM_NOT_USED.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteItemUnknownItem() {
        String testArgs = "...";
        Command testCommand = new Command(CommandType.item, testArgs);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.UNKNOWN_COMMAND.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteGatherNormal() {
        Command testCommand = new Command(CommandType.gather, null);
        String testField = "test";
        when(field.gatherItem(testID)).thenReturn(true);
        when(field.visualize()).thenReturn(testField);
        executor.addPlayer(testID);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.GATHER_SUCCESSFUL.get());
        assertEquals(answer.receivers().IDs(), Set.of(testID));
        assertEquals(answer.receivers().answer(), testField);
    }

    @Test
    void testExecuteGatherNoItem() {
        Command testCommand = new Command(CommandType.gather, null);
        when(field.gatherItem(testID)).thenReturn(false);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.GATHER_UNSUCCESSFUL.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteSendNormal() {
        String testID1 = "testID1";
        String testArgs = "12";
        int testItemNumber = 1;
        int testDisplayNumber = 2;
        Command testCommand = new Command(CommandType.send, testArgs);
        when(field.sendItem(testID, testItemNumber, testDisplayNumber)).thenReturn(testID1);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.ITEM_SENT.get());
        assertEquals(answer.receivers().IDs(), List.of(testID1));
        assertEquals(answer.receivers().answer(), Message.ITEM_RECEIVED.get());
    }

    @Test
    void testExecuteSendNoReceiverOnField() {
        String testArgs = "12";
        int testItemNumber = 1;
        int testDisplayNumber = 2;
        Command testCommand = new Command(CommandType.send, testArgs);
        when(field.sendItem(testID, testItemNumber, testDisplayNumber)).thenReturn(null);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.ITEM_NOT_SENT.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteSendUnknownArguments() {
        String testArgs = "...";
        Command testCommand = new Command(CommandType.send, testArgs);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.UNKNOWN_COMMAND.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteAttack() {
        Command testCommand = new Command(CommandType.attack, null);
        when(field.playerAttack(testID)).thenReturn(Set.of());

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.ATTACK.get());
        assertEquals(answer.receivers().IDs(), List.of());
        assertEquals(answer.receivers().answer(), Message.HIT.get());
    }

    @Test
    void testExecuteMoveNormal() {
        String directionString = "up";
        String test = "test";
        Command testCommand = new Command(CommandType.move, directionString);
        Direction testDirection = Direction.up;
        when(field.move(testID, testDirection)).thenReturn(true);
        when(field.visualize()).thenReturn(test);

        executor.addPlayer(testID);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertNull(answer.sender());
        assertEquals(answer.receivers().IDs(), Set.of(testID));
        assertEquals(answer.receivers().answer(), test);
    }

    @Test
    void testExecuteMoveObstacleInFront() {
        String directionString = "up";
        Command testCommand = new Command(CommandType.move, directionString);
        Direction testDirection = Direction.up;
        when(field.move(testID, testDirection)).thenReturn(false);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.UNABLE_MOVE.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteMoveUnknownDirection() {
        String testArgs = "...";
        Command testCommand = new Command(CommandType.move, testArgs);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.UNKNOWN_COMMAND.get());
        assertNull(answer.receivers());
    }

    @Test
    void testExecuteRespawn() {
        Command testCommand = new Command(CommandType.respawn, null);
        String test = "test";
        when(field.visualize()).thenReturn(test);

        executor.addPlayer(testID);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertNull(answer.sender());
        assertEquals(answer.receivers().IDs(), Set.of(testID));
        assertEquals(answer.receivers().answer(), test);
    }

    @Test
    void testExecuteUnknownCommand() {
        Command testCommand = new Command(CommandType.unknown, null);

        SenderReceivers answer = executor.execute(testCommand, testID);
        assertEquals(answer.sender().ID(), testID);
        assertEquals(answer.sender().answer(), Message.UNKNOWN_COMMAND.get());
        assertNull(answer.receivers());
    }

    @Test
    void testIsLimitReached() {
        assertFalse(executor.isLimitReached());

        final int LIMIT = 9;
        for (Integer i = 0; !executor.isLimitReached(); i++) {
            executor.addPlayer(i.toString());
        }
        assertEquals(executor.getClients().size(), LIMIT);
    }

    @Test
    void testAddPlayer() {
        Collection<String> collectionOfIDs = Set.of(testID);

        ReceiversAnswer testAnswer = executor.addPlayer(testID);
        assertEquals(testAnswer.IDs(), collectionOfIDs);
    }

    @Test
    void testAddTwoPlayers() {
        String testID1 = "testID1";
        Collection<String> collectionOfIDs = Set.of(testID, testID1);
        executor.addPlayer(testID);

        ReceiversAnswer testAnswer = executor.addPlayer(testID1);
        assertEquals(testAnswer.IDs(), collectionOfIDs);
    }

    @Test
    void testRemovePlayer() {
        executor.addPlayer(testID);

        executor.removePlayer(testID);
        assertEquals(executor.getClients().size(), 0);
    }

    @Test
    void testRemovePlayerFromMore() {
        String testID1 = "testID1";
        executor.addPlayer(testID);
        executor.addPlayer(testID1);
        Collection<String> collectionOfIDs = Set.of(testID1);

        executor.removePlayer(testID);
        assertEquals(executor.getClients(), collectionOfIDs);
    }

    @Test
    void testMinionAttack() {
        String testID1 = "testID1";
        int testInt = 1;
        Collection<String> testCollection = List.of(testID, testID1);
        when(field.minionAttack()).thenReturn(List.of(new Player(testID, testInt),
                new Player(testID1, testInt)));

        SenderReceivers answer = executor.minionAttack();
        assertNull(answer.sender());
        assertEquals(answer.receivers().IDs(), testCollection);
        assertEquals(answer.receivers().answer(), Message.HIT.get());
    }
}