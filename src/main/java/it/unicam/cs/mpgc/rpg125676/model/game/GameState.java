package it.unicam.cs.mpgc.rpg125676.model.game;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.game.decision.PendingDecision;

import java.util.Objects;
import java.util.Optional;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the mutable state of a single game session.
 * The class stores the main game entities, the current game status
 * and phase, the turn counter and the temporary state required by
 * gameplay mechanics such as noise targets, pending decisions,
 * active decoys, listening cooldown and recovery availability.
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final GameSettings settings;
    private final House house;
    private final Player player;
    private final Presence presence;

    private GameStatus status;
    private GamePhase phase;
    private PendingDecision pendingDecision;

    private Room noiseTarget;
    private int turnCount;
    private int listenAvailableFromTurn;
    private boolean recoveryAvailable;
    private PlacedDecoy placedDecoy;

    /**
     * Creates a new game state with the supplied configuration,
     * house, player and Presence.
     * The player starting room is marked as visited and both entities
     * are verified to belong to the supplied house. The game starts
     * in progress with turn counter set to zero.
     * The initial phase is determined from the starting positions
     * of the player and the Presence.
     *
     * @param settings configuration used by the game session
     * @param house house in which the game takes place
     * @param player player participating in the game
     * @param presence Presence hunting the player
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if the player or Presence
     *         is located in a room that does not belong to the house
     */
    public GameState(GameSettings settings, House house, Player player, Presence presence) {
        this.settings = Objects.requireNonNull(settings);
        this.house = Objects.requireNonNull(house);
        this.player = Objects.requireNonNull(player);
        this.presence = Objects.requireNonNull(presence);
        player.getCurrentRoom().markAsVisited();

        validateEntityRoom(player.getCurrentRoom());
        validateEntityRoom(presence.getCurrentRoom());

        this.status = GameStatus.IN_PROGRESS;
        this.turnCount = 0;
        this.listenAvailableFromTurn = 0;
        this.recoveryAvailable = false;
        synchronizePhaseWithPositions();
    }

    public GameSettings getSettings() {
        return settings;
    }

    public House getHouse() {
        return house;
    }

    public Player getPlayer() {
        return player;
    }

    public Presence getPresence() {
        return presence;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public boolean isFinished() {
        return status != GameStatus.IN_PROGRESS;
    }

    public boolean isPlayerWithPresence() {
        return player.getCurrentRoom().equals(presence.getCurrentRoom());
    }

    public Optional<Room> getNoiseTarget() {
        return Optional.ofNullable(noiseTarget);
    }

    /**
     * Sets the room that represents the current noise target
     * followed by the Presence.
     * The room must belong to the current game house.
     * @param room room in which the relevant noise was produced
     * @throws NullPointerException if room is null
     * @throws IllegalArgumentException if room does not belong
     *         to the current house
     */
    public void setNoiseTarget(Room room) {
        Objects.requireNonNull(room);
        validateEntityRoom(room);
        noiseTarget = room;
    }

    /**
     * Updates the current game phase according to the positions
     * of the player and the Presence.
     * If both entities occupy the same room, the phase becomes
     * {@link GamePhase#CONFRONTATION}; otherwise it becomes
     * {@link GamePhase#EXPLORATION}.
     * No update is performed after the game has ended.
     */
    public void synchronizePhaseWithPositions() {
        if (isFinished()) {
            return;
        }
        phase = isPlayerWithPresence() ? GamePhase.CONFRONTATION : GamePhase.EXPLORATION;
    }


    public void advanceTurn() {
        turnCount++;
    }

    public void win() {
        if (!isFinished()) {
            status = GameStatus.WON;
        }
    }

    public void lose() {
        if (!isFinished()) {
            status = GameStatus.LOST;
        }
    }

    /**
     * Validates that the specified room belongs to the current house.
     *
     * @param room room to validate
     * @throws IllegalArgumentException if the room does not belong to the house
     */
    private void validateEntityRoom(Room room) {
        if (!house.getRooms().contains(room)) {
            throw new IllegalArgumentException("Room does not belong to the game house");
        }
    }

    public boolean hasPendingDecision() {
        return pendingDecision != null;
    }

    public Optional<PendingDecision> getPendingDecision() {
        return Optional.ofNullable(pendingDecision);
    }

    /**
     * Registers a decision that must be resolved by the player.
     * Only one decision may be pending at a time.
     * @param decision decision to register
     * @throws NullPointerException if decision is null
     * @throws IllegalStateException if another decision is already pending
     */
    public void requestDecision(PendingDecision decision) {
        Objects.requireNonNull(decision);

        if (pendingDecision != null) {throw new IllegalStateException("Another decision is already pending");}
        pendingDecision = decision;
    }

    /**
     * Removes the currently pending decision after it has been resolved.
     * @throws IllegalStateException if no decision is currently pending
     */
    public void clearPendingDecision() {
        if (pendingDecision == null) {
            throw new IllegalStateException("There is no pending decision");}
        pendingDecision = null;
    }
    public boolean hasPlacedDecoy() {
        return placedDecoy != null;
    }

    public Optional<PlacedDecoy> getPlacedDecoy() {
        return Optional.ofNullable(placedDecoy);
    }

    /**
     * Registers the specified decoy as the currently placced decoy.
     * @param decoy decoy to place
     * @throws NullPointerException if decoy is null
     * @throws IllegalStateException if another decoy is already active
     */
    public void placeDecoy(PlacedDecoy decoy) {
        Objects.requireNonNull(decoy);
        if (placedDecoy != null) {throw new IllegalStateException("A decoy is already placed.");}
        placedDecoy = decoy;
    }

    /**
     * Checks whether a placed decoy is currently controlling
     * the Presence's behavior.
     *
     * @return true if the decoy is currently ringing
     */
    public boolean isDecoyActive() {
        return placedDecoy != null && placedDecoy.isRinging();
    }

    public void clearPlacedDecoy() {
        placedDecoy = null;
    }

    public boolean canListen() {
        return turnCount >= listenAvailableFromTurn;
    }

    public int getListenCooldownRemaining() {
        return Math.max(0, listenAvailableFromTurn - turnCount);
    }

    public void startListenCooldown() {
        listenAvailableFromTurn = turnCount + settings.listenCooldownTurns();
    }

    public boolean isRecoveryAvailable() {
        return recoveryAvailable;
    }

    public void enableRecovery() {
        recoveryAvailable = true;
    }

    public void consumeRecovery() {
        recoveryAvailable = false;
    }
}