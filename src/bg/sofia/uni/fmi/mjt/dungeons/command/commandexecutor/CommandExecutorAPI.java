package bg.sofia.uni.fmi.mjt.dungeons.command.commandexecutor;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;
import bg.sofia.uni.fmi.mjt.dungeons.command.Command;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.ReceiversAnswer;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.SenderReceivers;

import java.util.Collection;

public interface CommandExecutorAPI {

    /**
     * executes the command by the name of the player with the given ID
     *
     * @param cmd command to be executed
     * @param ID  player's identity number
     * @return response to all the necessary players
     */
    SenderReceivers execute(Command cmd, String ID);

    /**
     * check if the limit of the players on the field is reached
     *
     * @return true if there is no empty space for new player on the field,
     * false otherwise
     */
    boolean isLimitReached();

    /**
     * @return collection of clients
     */
    Collection<String> getClients();

    /**
     * adds new player to the field
     *
     * @param ID player's identity number
     * @return response to all players containing the new look of the field
     */
    ReceiversAnswer addPlayer(String ID);

    /**
     * removes player from the field
     *
     * @param ID player's identity number
     * @return response to all players containing the new look of the field
     */
    ReceiversAnswer removePlayer(String ID);

    /**
     * connector getter
     *
     * @return connector, connected to the minions
     */
    Connector getConnector();

    /**
     * @return response to all players hit by a minion
     */
    SenderReceivers minionAttack();
}
