package it.unicam.cs.mpgc.rpg125676.persistence.leaderboard;

import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;

import java.util.List;

/**
 * Defines persistence operations for leaderboard entries.
 */
public interface LeaderboardRepository {

    /**
     * Returns all entries in leaderboard ranking order.
     */
    List<LeaderboardEntry> findAll();

    /**
     * Adds a new completed game to the leaderboard.
     */
    void add(LeaderboardEntry entry);

    /**
     * Removes every leaderboard entry.
     */
    void clear();
}
