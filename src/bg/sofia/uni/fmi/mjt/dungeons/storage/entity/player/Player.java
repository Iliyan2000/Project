package bg.sofia.uni.fmi.mjt.dungeons.storage.entity.player;

import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Actor;
import bg.sofia.uni.fmi.mjt.dungeons.storage.entity.Stats;
import bg.sofia.uni.fmi.mjt.dungeons.storage.treasure.Treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player implements Comparable<Player>, Actor, PlayerAPI {

    public static final int MAX_EXPERIENCE = 100;
    public static final int BACKPACK_LIMIT = 10;

    private final String ID;
    private final int displayNumber;
    private final List<Treasure> backpack;
    private Stats stats;
    private int experience;

    public Player(String ID, int displayNumber) {
        this.ID = ID;
        this.displayNumber = displayNumber;
        experience = 0;
        stats = new Stats(1);
        backpack = new ArrayList<>(BACKPACK_LIMIT);
    }

    public int getDisplayNumber() {
        return displayNumber;
    }

    public String getID() {
        return ID;
    }

    public Stats stats() {
        return stats;
    }

    public int getExperience() {
        return experience;
    }

    public List<Treasure> getBackpack() {
        return backpack;
    }

    public boolean use(int itemNumber) {
        Treasure item;
        try {
            item = backpack.get(itemNumber - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        if (item.use()) {
            backpack.remove(itemNumber - 1);
            return true;
        }
        return false;
    }

    public boolean gather(Treasure treasure) {
        if (backpack.size() == BACKPACK_LIMIT ||
                treasure == null) {
            return false;
        }

        treasure.assign(stats);
        backpack.add(treasure);
        return true;
    }

    public Treasure drop(int itemNumber) {
        if (itemNumber > backpack.size()) {
            return null;
        }
        return backpack.remove(itemNumber - 1);
    }

    public Treasure dropRandom() {
        if (backpack.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        return drop(rand.nextInt(backpack.size()) + 1);
    }

    public void resetStats() {
        stats = new Stats(1);
    }

    public void takeExperience(int experience) {
        this.experience += experience;
        levelUp();
    }

    public int attack() {
        return stats.attack();
    }

    public void takeDamage(int damage) {
        stats.subtractHealth(damage - stats.defence());
    }

    public boolean isDead() {
        return !(stats.health() > 0);
    }

    @Override
    public int compareTo(Player o) {
        return displayNumber - o.displayNumber;
    }

    private void levelUp() {
        if (experience < MAX_EXPERIENCE) {
            return;
        }

        stats.levelUp();
        experience -= MAX_EXPERIENCE;
    }
}
