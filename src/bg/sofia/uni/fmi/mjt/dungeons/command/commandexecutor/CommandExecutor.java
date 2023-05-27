package bg.sofia.uni.fmi.mjt.dungeons.command.commandexecutor;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;
import bg.sofia.uni.fmi.mjt.dungeons.command.Command;
import bg.sofia.uni.fmi.mjt.dungeons.command.CommandType;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.Message;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.ReceiversAnswer;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.SenderAnswer;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.SenderReceivers;
import bg.sofia.uni.fmi.mjt.dungeons.storage.Direction;
import bg.sofia.uni.fmi.mjt.dungeons.storage.field.Field;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player.Player;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CommandExecutor implements CommandExecutorAPI {
    private static final int argumentsDivisor = 10;
    private static final int LIMIT = 9;
    private final Field field;
    Collection<String> clients;

    public CommandExecutor(Field field) {
        this.field = field;
        clients = new HashSet<>();
    }

    public SenderReceivers execute(Command cmd, String ID) {
        try {
            return switch (cmd.command()) {
                case help -> new SenderReceivers(new SenderAnswer(ID, Message.HELP.get()), null);

                case stats -> new SenderReceivers(statusAnswer(ID), null);

                case inventory -> new SenderReceivers(inventoryAnswer(ID), null);

                case item -> field.useItem(ID, Integer.parseInt(cmd.argument())) ?
                        new SenderReceivers(new SenderAnswer(ID, Message.ITEM_USED.get()), null) :
                        new SenderReceivers(new SenderAnswer(ID, Message.ITEM_NOT_USED.get()), null);

                case gather -> field.gatherItem(ID) ?
                        new SenderReceivers(new SenderAnswer(ID, Message.GATHER_SUCCESSFUL.get()),
                                new ReceiversAnswer(clients, field.visualize())) :
                        new SenderReceivers(new SenderAnswer(ID, Message.GATHER_UNSUCCESSFUL.get()), null);

                case send -> send(ID, cmd.argument());

                case attack -> new SenderReceivers(new SenderAnswer(ID, Message.ATTACK.get()), attackVictims(ID));

                case move -> field.move(ID, Direction.valueOf(cmd.argument())) ?
                        new SenderReceivers(null, new ReceiversAnswer(clients, field.visualize())) :
                        new SenderReceivers(new SenderAnswer(ID, Message.UNABLE_MOVE.get()), null);

                case respawn -> respawnPlayer(ID);

                case unknown -> new SenderReceivers(new SenderAnswer(ID, Message.UNKNOWN_COMMAND.get()), null);
            };
        } catch (IllegalArgumentException e) {
            return execute(new Command(CommandType.unknown, null), ID);
        }
    }

    public boolean isLimitReached() {
        return clients.size() == LIMIT;
    }

    public Collection<String> getClients() {
        return clients;
    }

    public ReceiversAnswer addPlayer(String ID) {
        clients.add(ID);
        field.addPlayer(ID);

        return new ReceiversAnswer(clients, field.visualize());
    }

    public ReceiversAnswer removePlayer(String ID) {
        clients.remove(ID);
        field.removePlayer(ID);

        return new ReceiversAnswer(clients, field.visualize());
    }

    public Connector getConnector() {
        return field.connector();
    }

    public SenderReceivers minionAttack() {
        Collection<Player> victims = field.minionAttack();
        List<String> victimsIDs = victims.stream().map(Player::getID).toList();

        return new SenderReceivers(null, new ReceiversAnswer(victimsIDs, Message.HIT.get()));
    }

    private SenderAnswer statusAnswer(String ID) {
        Stats stats = field.getStats(ID);

        String reply = String.format(Message.STATS.get(),
                stats.level(),
                field.getExperience(ID), Player.MAX_EXPERIENCE,
                stats.health(),
                stats.mana(),
                stats.attack(),
                stats.defence());

        return new SenderAnswer(ID, reply);
    }

    private SenderAnswer inventoryAnswer(String ID) {
        StringBuilder result = new StringBuilder();
        List<Treasure> backpack = field.getInventory(ID);

        for (int i = 0; i < Player.BACKPACK_LIMIT; i++) {
            result.append(i + 1).append(Message.DOT.get());

            try {
                result.append(backpack.get(i).mainInformation());
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return new SenderAnswer(ID, result.toString());
    }

    private SenderReceivers send(String ID, String args) {

        int itemNumber = Integer.parseInt(args) / argumentsDivisor;
        int receiverNumber = Integer.parseInt(args) % argumentsDivisor;
        String receiverID = field.sendItem(ID, itemNumber, receiverNumber);

        if (receiverID == null) {
            return new SenderReceivers(new SenderAnswer(ID, Message.ITEM_NOT_SENT.get()), null);
        }
        return new SenderReceivers(new SenderAnswer(ID, Message.ITEM_SENT.get()),
                new ReceiversAnswer(List.of(receiverID), Message.ITEM_RECEIVED.get()));
    }

    private ReceiversAnswer attackVictims(String ID) {
        Collection<Player> victims = field.playerAttack(ID);
        List<String> victimsIDs = victims.stream().map(Player::getID).toList();

        return new ReceiversAnswer(victimsIDs, Message.HIT.get());
    }

    private SenderReceivers respawnPlayer(String ID) {
        field.respawn(ID);
        return new SenderReceivers(null, new ReceiversAnswer(clients, field.visualize()));
    }
}
