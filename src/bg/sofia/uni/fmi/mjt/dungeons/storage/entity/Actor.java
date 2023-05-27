package bg.sofia.uni.fmi.mjt.dungeons.storage.entity;

public interface Actor {

    /**
     * stats getter
     *
     * @return the Stats of the Player
     */
    Stats stats();

    /**
     * attack getter
     *
     * @return the attack of the Actor
     */
    int attack();

    /**
     * take certain amount of damage
     *
     * @param damage that is taken
     */
    void takeDamage(int damage);

    /**
     * death check
     *
     * @return true the health of the Actor is below or equal 0
     */
    boolean isDead();
}
