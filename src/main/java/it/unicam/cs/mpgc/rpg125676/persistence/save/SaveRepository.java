package it.unicam.cs.mpgc.rpg125676.persistence.save;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

/**
 * Defines the persistence operations required to manage
 * a saved game session.
 */
public interface SaveRepository {
    /**
     * Checks whether a saved game is currently available.
     * @return true if a saved game exists, false otherwise
     */
    boolean exists();

    /**
     * Stores the supplied game state
     * @param state game state to persist
     */
    void save(GameState state);

    /**
     * Restores the previously saved game state.
     * @return the restored game state
     */
    GameState load();

    /**
     * Deletes the currently stored game save, if present.
     */
    void delete();
}
