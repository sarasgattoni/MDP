package it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory;

import java.io.Serial;
import java.io.Serializable;

/**
 * Stores and manages the decoys collected by the player.
 *
 * The inventory keeps track of the number of available decoys
 * and provides operations to add, check and consume them.
 */
public class PlayerInventory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int decoyCount;

    /**
     * Creates an empty player inventory.
     */
    public PlayerInventory() {
        this.decoyCount = 0;
    }


    public int getDecoyCount() {
        return decoyCount;
    }

    public void addDecoy() {
        decoyCount++;
    }

    /**
     * Checks whether at least one decoy is available.
     *
     * @return true if the inventory contains a decoy
     */
    public boolean hasDecoy() {
        return decoyCount > 0;
    }

    /**
     * Consumes one decoy if available.
     *
     * @return true if a decoy was consumed, false otherwise
     */
    public boolean consumeDecoy() {
        if (!hasDecoy()) {return false;}

        decoyCount--;
        return true;
    }
}