package it.unicam.cs.mpgc.rpg125676.persistence.leaderboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;
import it.unicam.cs.mpgc.rpg125676.persistence.PersistenceException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores leaderboard entries in a JSON file using Gson.
 * Entries are returned and persisted according to the ranking
 * defined by {@link LeaderboardEntry#RANKING}.
 * The repository creates the required parent directory when
 * leaderboard data must be written.
 */
public class JsonLeaderboardRepository implements LeaderboardRepository {

    private static final Type ENTRY_LIST_TYPE = new TypeToken<List<LeaderboardEntry>>() {}.getType();

    private final Path filePath;
    private final Gson gson;

    /**
     * Creates a leaderboard repository for the supplied file path.
     * A Gson instance is used to store leaderboard data in JSON format.
     *
     * @param filePath path of the leaderboard JSON file
     * @throws NullPointerException if filePath is null
     */
    public JsonLeaderboardRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Reads all leaderboard entries from the configured JSON file.
     * If the file does not exist or contains no entries, an empty list
     * is returned. Loaded entries are sorted according to the
     * leaderboard ranking before being returned.
     * @return leaderboard entries in ranking order
     * @throws PersistenceException if the file cannot be read
     *         or contains invalid JSON
     */
    @Override
    public List<LeaderboardEntry> findAll() {
        if (!Files.isRegularFile(filePath)) {
            return List.of();
        }
        try (
                Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)
        ) {
            List<LeaderboardEntry> entries = gson.fromJson(reader, ENTRY_LIST_TYPE);

            if (entries == null) {
                return List.of();
            }
            return entries.stream().sorted(LeaderboardEntry.RANKING).toList();

        } catch (IOException | JsonParseException exception) {
            throw new PersistenceException("Unable to read leaderboard from " + filePath, exception);
        }
    }

    /**
     * Adds the supplied entry to the leaderboard.
     * Existing entries are loaded, the new result is added and the
     * complete collection is sorted.
     *
     * @param entry leaderboard entry to add
     * @throws NullPointerException if entry is null
     * @throws PersistenceException if leaderboard data cannot be
     *         read or written
     */
    @Override
    public void add(LeaderboardEntry entry) {
        Objects.requireNonNull(entry);
        List<LeaderboardEntry> entries = new ArrayList<>(findAll());
        entries.add(entry);
        entries.sort(LeaderboardEntry.RANKING);
        write(entries);
    }

    /**
     * Removes all leaderboard entries by replacing the stored
     * collection with an empty one.
     *
     * @throws PersistenceException if the leaderboard file
     *         cannot be written
     */
    @Override
    public void clear() {
        write(List.of());
    }

    /**
     * Writes the supplied leaderboard entries to the configured JSON file.
     * The required parent directory is created before writing.
     *
     * @param entries entries to persist
     * @throws PersistenceException if the data cannot be written
     */
    private void write(List<LeaderboardEntry> entries) {
        createParentDirectory();
        try (
                Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)
        ) {
            gson.toJson(entries, ENTRY_LIST_TYPE, writer);
        } catch (IOException exception) {
            throw new PersistenceException("Unable to write leaderboard to " + filePath, exception);
        }
    }

    /**
     * Creates the parent directory of the leaderboard file when necessary.
     * No operation is performed when the configured path has no parent.
     *
     * @throws PersistenceException if the directory cannot be created
     */
    private void createParentDirectory() {
        Path parent = filePath.getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);

        } catch (IOException exception) {
            throw new PersistenceException("Unable to create leaderboard directory " + parent, exception);
        }
    }
}
