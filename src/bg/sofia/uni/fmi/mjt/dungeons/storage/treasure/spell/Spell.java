package bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.spell;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;

public abstract class Spell implements Treasure {
    protected static final int baseStats = 10;
    protected static final int manaCost = 20;

    protected int level;
    protected Stats ownerStats;

    public Spell(int level) {
        this.level = level;
        ownerStats = null;
    }

    public void assign(Stats ownerStats) {
        this.ownerStats = ownerStats;
    }

    public int level() {
        return level;
    }
}
