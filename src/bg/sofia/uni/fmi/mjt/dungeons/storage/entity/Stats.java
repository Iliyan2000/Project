package bg.sofia.uni.fmi.mjt.dungeons.storage.entity;

public class Stats {
    private static final int START_HEALTH = 100;
    private static final int LEVEL_UP_HEALTH = 10;
    private static final int START_MANA = 100;
    private static final int LEVEL_UP_MANA = 10;
    private static final int START_ATTACK = 50;
    private static final int LEVEL_UP_ATTACK = 5;
    private static final int START_DEFENCE = 30;
    private static final int LEVEL_UP_DEFENCE = 5;

    private int level;
    private int health;
    private int mana;
    private int attack;
    private int defence;

    public Stats(int level) {
        this.level = level;
        health  = START_HEALTH  + (level - 1) * LEVEL_UP_HEALTH;
        mana    = START_MANA    + (level - 1) * LEVEL_UP_MANA;
        attack  = START_ATTACK  + (level - 1) * LEVEL_UP_ATTACK;
        defence = START_DEFENCE + (level - 1) * LEVEL_UP_DEFENCE;
    }

    public int level() {
        return level;
    }
    public int health() {
        return health;
    }
    public int mana() {
        return mana;
    }
    public int attack() {
        return attack;
    }
    public int defence() {
        return defence;
    }

    public void levelUp() {
        level   ++;
        health  += LEVEL_UP_HEALTH;
        mana    += LEVEL_UP_MANA;
        attack  += LEVEL_UP_ATTACK;
        defence += LEVEL_UP_DEFENCE;
    }

    public void addHealth(int amount) {
        health += amount;
    }
    public void addMana(int amount) {
        mana += amount;
    }
    public void addAttack(int amount) {
        attack += amount;
    }

    public void subtractHealth(int amount) {
        health -= amount;
    }
    public void subtractMana(int amount) {
        mana -= amount;
    }
}
