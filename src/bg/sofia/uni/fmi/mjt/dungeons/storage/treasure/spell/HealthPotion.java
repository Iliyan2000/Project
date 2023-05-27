package bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.spell;

import bg.sofia.uni.fmi.mjt.dungeons.command.answer.Message;

public class HealthPotion extends Spell {
    private static final String className = "HealthPotion";

    public HealthPotion(int level) {
        super(level);
    }

    public boolean use() {
        if (ownerStats.level() < level) {
            return false;
        }
        ownerStats.subtractMana(manaCost);
        ownerStats.addHealth(baseStats * level);
        return true;
    }

    public String mainInformation() {
        return String.format(Message.ITEM_CHARACTERISTICS.get(), className, level, manaCost);
    }
}
