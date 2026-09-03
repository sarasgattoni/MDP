package it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory;

import java.io.Serializable;

/**
 * Defines the player's inventory operations.
 */
public interface Inventory extends Serializable {

    int getDecoyCount();

    void addDecoy();

    boolean consumeDecoy();

    default boolean hasDecoy() {
        return getDecoyCount() > 0;
    }
}
