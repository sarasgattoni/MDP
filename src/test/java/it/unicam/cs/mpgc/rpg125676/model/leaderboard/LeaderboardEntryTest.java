package it.unicam.cs.mpgc.rpg125676.model.leaderboard;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardEntryTest {

    @Test
    void fewerTurnsShouldRankFirst() {
        LeaderboardEntry slower = new LeaderboardEntry("Slower", 40, 10, 1000);
        LeaderboardEntry faster = new LeaderboardEntry("Faster", 30, 5, 2000);
        List<LeaderboardEntry> entries = new ArrayList<>(List.of(slower, faster));
        entries.sort(LeaderboardEntry.RANKING);

        assertEquals(faster, entries.getFirst());
    }

    @Test
    void higherLucidityShouldBreakTurnTie() {
        LeaderboardEntry lowLucidity = new LeaderboardEntry("Low", 30, 3, 1000);
        LeaderboardEntry highLucidity = new LeaderboardEntry("High", 30, 8, 2000);
        List<LeaderboardEntry> entries = new ArrayList<>(List.of(lowLucidity, highLucidity));
        entries.sort(LeaderboardEntry.RANKING);

        assertEquals(highLucidity, entries.getFirst());
    }
}