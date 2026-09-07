package it.unicam.cs.mpgc.rpg125676.persistence.save;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.persistence.PersistenceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Stores and restores complete game sessions using Java Serialization.
 * The repository persists the supplied {@link GameState} to a file
 * and reconstructs it when the game is loaded.
 * Games with a pending player decision cannot be saved because
 * their current turn has not yet been completely resolved.
 */
public class SerializedSaveRepository implements SaveRepository {
    private final Path savePath;

    /**
     * Creates a save repository using the supplied file path.
     *
     * @param savePath path used to store the serialized game state
     * @throws NullPointerException if savePath is null
     */
    public SerializedSaveRepository(Path savePath) {
        this.savePath =Objects.requireNonNull(savePath);
    }

    /**
     * Checks whether the configured save path contains a regular file.
     *
     * @return true if a save file exists, false otherwise
     */
    @Override
    public boolean exists() {
        return Files.isRegularFile(savePath);
    }

    /**
     * Serializes the supplied game state to the configured save file.
     * The required parent directory is created automatically when
     * necessary. A game cannot be saved while a player decision
     * is still pending.
     *
     * @param state game state to persist
     * @throws NullPointerException if state is null
     * @throws IllegalStateException if a player decision is pending
     * @throws PersistenceException if the state cannot be written
     */
    @Override
    public void save(GameState state) {
        Objects.requireNonNull(state);

        if (state.hasPendingDecision()) {
            throw new IllegalStateException("The game cannot be saved while " + "a player decision is pending");
        }
        createParentDirectory();

        try (
                ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(savePath))
        ) {
            output.writeObject(state);
        } catch (IOException exception) {
            throw new PersistenceException("Unable to save game to " + savePath,exception);
        }
    }

    /**
     * Restores the game state stored in the configured save file.
     *
     * @return the deserialized game state
     * @throws PersistenceException if the save file does not exist,
     *         cannot be read or does not contain a valid GameState
     */
    @Override
    public GameState load() {
        if (!exists()) {
            throw new PersistenceException("Save file does not exist: " + savePath);
        }

        try (
                ObjectInputStream input = new ObjectInputStream(Files.newInputStream(savePath))
        ) {
            Object object = input.readObject();
            if (!(object instanceof GameState state)) {
                throw new PersistenceException("Save file does not contain a valid game state");
            }
            return state;

        } catch (IOException | ClassNotFoundException exception) {
            throw new PersistenceException("Unable to load game from " + savePath, exception);
        }
    }

    /**
     * Deletes the configured save file if it exists.
     *
     * @throws PersistenceException if the file cannot be deleted
     */
    @Override
    public void delete() {
        try {
            Files.deleteIfExists(savePath);
        } catch (IOException exception) {
            throw new PersistenceException("Unable to delete save file " + savePath, exception);
        }
    }

    /**
     * Creates the parent directory of the save file when necessary.
     *
     * No operation is performed when the configured path has no parent.
     *
     * @throws PersistenceException if the directory cannot be created
     */
    private void createParentDirectory() {
        Path parent = savePath.getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new PersistenceException("Unable to create save directory " + parent, exception);
        }
    }
}
