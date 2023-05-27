package bg.sofia.uni.fmi.mjt.dungeons.storage.treasure;

import bg.sofia.uni.fmi.mjt.dungeons.command.answer.Message;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;

public class Weapon implements Treasure {
    private static final String className = "Weapon";
    private static final int baseDamage = 5;
    private int level;
    private Stats ownerStats;

    public Weapon(int level) {
        this.level = level;
        ownerStats = null;
    }


    public void assign(Stats ownerStats) {
        this.ownerStats = ownerStats;
    }

    public boolean use() {
        if (ownerStats.level() < level) {
            return false;
        }
        ownerStats.addAttack(baseDamage * level);
        return true;
    }

    public int level() {
        return level;
    }

    public String mainInformation() {
        return String.format(Message.ITEM_CHARACTERISTICS.get(), className, level, 0);
    }
}
