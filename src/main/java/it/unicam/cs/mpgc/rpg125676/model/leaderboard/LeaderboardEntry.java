package it.unicam.cs.mpgc.rpg125676.model.leaderboard;

import java.util.Comparator;

/**
 * Represents a completed winning game stored
 * in the leaderboard.
 * Each entry records the player name, the number of turns required
 * to win, the remaining Lucidity and the completion timestamp.
 *
 * @param playerName player name
 * @param turns number of turns used to win
 * @param remainingLucidity lucidity remaining at victory
 * @param completedAtEpochMillis completion timestamp
 */
public record LeaderboardEntry(String playerName, int turns, int remainingLucidity, long completedAtEpochMillis) {

    /**
     * Defines the ordering used by the leaderboard:
     * entries with fewer turns are ranked first; when the number
     * of turns is equal, higher remaining Lucidity has priority.
     * If both values are equal, the earlier completion comes first.
     */
    public static final Comparator<LeaderboardEntry> RANKING = Comparator.comparingInt(LeaderboardEntry::turns).thenComparing(Comparator.comparingInt(LeaderboardEntry::remainingLucidity).reversed()).thenComparingLong(LeaderboardEntry::completedAtEpochMillis);

    /**
     * Validates and normalizes the values of a leaderboard entry.
     *
     * @throws IllegalArgumentException if the player name is null or blank,
     *         if turns is not positive, if remaining Lucidity is negative
     *         or if the completion timestamp is not positive
     */
    public LeaderboardEntry {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be blank");
        }
        if (turns <= 0) {
            throw new IllegalArgumentException("Turns must be greater than zero");
        }
        if (remainingLucidity < 0) {
            throw new IllegalArgumentException("Remaining lucidity cannot be negative");
        }
        if (completedAtEpochMillis <= 0) {
            throw new IllegalArgumentException("Completion timestamp must be positive");
        }
        playerName = playerName.trim();
    }
}