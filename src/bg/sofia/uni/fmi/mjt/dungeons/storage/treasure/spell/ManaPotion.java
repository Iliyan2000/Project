package bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.spell;

import bg.sofia.uni.fmi.mjt.dungeons.command.answer.Message;

public class ManaPotion extends Spell {
    private static final String className = "ManaPotion";

    public ManaPotion(int level) {
        super(level);
    }

    public boolean use() {
        if (ownerStats.level() < level) {
            return false;
        }
        ownerStats.subtractMana(manaCost);
        ownerStats.addMana(baseStats * level);
        return true;
    }

    public String mainInformation() {
        return String.format(Message.ITEM_CHARACTERISTICS.get(), className, level, manaCost);
    }
}
