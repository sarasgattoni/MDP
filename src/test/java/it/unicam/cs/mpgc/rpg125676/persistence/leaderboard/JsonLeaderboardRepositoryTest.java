package it.unicam.cs.mpgc.rpg125676.persistence.leaderboard;

import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonLeaderboardRepositoryTest {

    @TempDir
    Path tempDirectory;

    private Path leaderboardPath;
    private LeaderboardRepository repository;

    @BeforeEach
    void setUp() {
        leaderboardPath = tempDirectory.resolve("leaderboard.json");
        repository = new JsonLeaderboardRepository(leaderboardPath);
    }

    @Test
    void missingFileShouldReturnEmptyLeaderboard() {
        List<LeaderboardEntry> entries = repository.findAll();

        assertTrue(entries.isEmpty());
    }

    @Test
    void shouldPersistAddedEntry() {
        LeaderboardEntry entry = new LeaderboardEntry("Sarah", 30, 7, 1000);
        repository.add(entry);

        assertTrue(java.nio.file.Files.isRegularFile(leaderboardPath));

        List<LeaderboardEntry> entries = repository.findAll();

        assertEquals(1, entries.size());
        assertEquals(entry, entries.getFirst());
    }

    @Test
    void shouldOrderEntriesByNumberOfTurns() {
        LeaderboardEntry slower = new LeaderboardEntry("Slow", 40, 10, 1000);
        LeaderboardEntry faster = new LeaderboardEntry("Fast", 25, 4, 2000
        );
        repository.add(slower);
        repository.add(faster);
        List<LeaderboardEntry> entries = repository.findAll();

        assertEquals(faster, entries.getFirst());
        assertEquals(slower, entries.get(1));
    }

    @Test
    void higherLucidityShouldBreakTurnTie() {
        LeaderboardEntry lowerLucidity = new LeaderboardEntry("Low", 30, 3, 1000);
        LeaderboardEntry higherLucidity = new LeaderboardEntry("High", 30, 8, 2000);
        repository.add(lowerLucidity);
        repository.add(higherLucidity);
        List<LeaderboardEntry> entries = repository.findAll();

        assertEquals(higherLucidity, entries.getFirst());
    }

    @Test
    void entriesShouldRemainAfterCreatingNewRepositoryInstance() {
        LeaderboardEntry entry = new LeaderboardEntry("Sarah", 30, 7, 1000);
        repository.add(entry);
        LeaderboardRepository anotherRepository = new JsonLeaderboardRepository(leaderboardPath);
        List<LeaderboardEntry> entries = anotherRepository.findAll();

        assertEquals(List.of(entry), entries);
    }

    @Test
    void clearShouldRemoveAllEntries() {
        repository.add(new LeaderboardEntry("Sarah", 30, 7, 1000));
        repository.clear();

        assertTrue(repository.findAll().isEmpty());
    }
}