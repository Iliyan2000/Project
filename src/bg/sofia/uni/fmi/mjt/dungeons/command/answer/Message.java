package bg.sofia.uni.fmi.mjt.dungeons.command.answer;

public enum Message {
    HELP("""
            - help                              - opens help menu
            - stats                             - show the stats of your character
            - inventory                         - show the items in your backpack
            - item <item_number>                - activates the <item_number> item in your backpack
            - gather                            - gathers the item from the ground
            - send <item_number><player_number> - sends <item_number> to <player_number> player, providing you are on same spot on the map
            - attack                            - attacks all entities on the same spot on the map
            - move {up,left,down,right}         - moves your character one position in {up,left,down,right} direction
            - respawn                           - move your character to a random free position on the map
            - quit                              - to exit the game"""),
    STATS("""
            level:      %d
            experience: %d/%d
            health:     %d
            mana:       %d
            attack:     %d
            defence:    %d"""),
    DOT("."),
    ITEM_CHARACTERISTICS("""
                            %s:
                            Level: %d
                            ManaCost: %d"""),
    ITEM_USED("Item successfully activated"),
    ITEM_NOT_USED("Unsuccessful activation of item"),
    GATHER_SUCCESSFUL("Item gathered"),
    GATHER_UNSUCCESSFUL("You can't gather right now"),
    ITEM_SENT("Item sent successfully"),
    ITEM_NOT_SENT("Not allowed to send item"),
    ITEM_RECEIVED("You just received an item"),
    ATTACK("You just attacked"),
    HIT("You just have been hit"),
    UNABLE_MOVE("You are unable to move."),
    UNKNOWN_COMMAND("Unknown command");

    private String message;

    Message(String message) {
        this.message = message;
    }

    public String get() {
        return message + System.lineSeparator();
    }
}
