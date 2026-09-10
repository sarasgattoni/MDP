package it.unicam.cs.mpgc.rpg125676.model.entity.player;

import it.unicam.cs.mpgc.rpg125676.model.entity.Entity;
import it.unicam.cs.mpgc.rpg125676.model.entity.behavior.Movable;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.Objects;

/**
 * Represents the player controlled by the user during a game session.
 * The player is a game-world {@link Entity} and a {@link Movable}
 * object, since it has an identity and occupies a room inside the house.
 * It has an inventory of items that can be collected during the game and
 * statistics that describe its ability to survive and fight. This class
 * also keeps track of the number of memories found in order to win the game.
 */
public class Player implements Entity, Movable {

    private  String name;
    private  PlayerStats stats;
    private  PlayerInventory inventory;
    private int maxMemories;

    private Room currentRoom;
    private int memoriesFound;

    public Player(String name, PlayerStats stats, PlayerInventory inventory, Room startingRoom, int maxMemories) {
        if (name == null || name.isBlank()) {throw new IllegalArgumentException("Player name cannot be blank");}

        if (maxMemories <= 0) {throw new IllegalArgumentException("Maximum memories must be greater than zero");}

        this.name = name;
        this.stats = Objects.requireNonNull(stats);
        this.inventory = Objects.requireNonNull(inventory);
        this.currentRoom = Objects.requireNonNull(startingRoom);
        this.maxMemories = maxMemories;
        this.memoriesFound = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public void moveTo(Room destination) {
        currentRoom = Objects.requireNonNull(destination);
    }

    public PlayerStats getStats() {
        return stats;
    }

    public int getMemoriesFound() {
        return memoriesFound;
    }

    public boolean hasAllMemories() {
        return memoriesFound >= maxMemories;
    }

    public void registerMemoryFound() {
        if (!hasAllMemories()) {
            memoriesFound++;
        }
    }

    public int getDecoyCount() {
        return inventory.getDecoyCount();
    }

    public boolean hasDecoy() {
        return inventory.hasDecoy();
    }

    public void collectDecoy() {
        inventory.addDecoy();
    }

    public boolean consumeDecoy() {
        return inventory.consumeDecoy();
    }
}
