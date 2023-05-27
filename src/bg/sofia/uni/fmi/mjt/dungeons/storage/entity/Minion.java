package bg.sofia.uni.fmi.mjt.dungeons.storage.entity;

import bg.sofia.uni.fmi.mjt.dungeons.Connector;

public class Minion extends Thread implements Actor {

    private int attackSpeedInMillis = 3_000;

    private Stats stats;
    private Connector connector;

    public Minion(int level, Connector connector) {
        stats = new Stats(level);
        this.connector = connector;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(attackSpeedInMillis);
                connector.setCauser(this);
                connector.trigger();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public Stats stats() {
        return stats;
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

    public int level() {
        return stats.level();
    }
}
