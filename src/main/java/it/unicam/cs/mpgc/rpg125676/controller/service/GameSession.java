package it.unicam.cs.mpgc.rpg125676.controller.service;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameStatus;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.game.factory.GameFactory;
import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;
import it.unicam.cs.mpgc.rpg125676.persistence.leaderboard.LeaderboardRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.save.SaveRepository;

import java.util.List;
import java.util.Objects;

/**
 * Coordinates the life cycle of the current game session.
 * The session creates and restores game engines, persists unfinished
 * games and records completed victories in the leaderboard.
 */
public class GameSession {

    private final GameFactory gameFactory;
    private final SaveRepository saveRepository;
    private final LeaderboardRepository leaderboardRepository;
    private GameEngine currentGame;
    private boolean resultRecorded;

    /**
     * Creates a game session using the supplied application services.
     *
     * @param gameFactory factory used to create and restore game engines
     * @param saveRepository repository used to persist game sessions
     * @param leaderboardRepository repository used to persist leaderboard entries
     * @throws NullPointerException if any dependency is null
     */
    public GameSession(GameFactory gameFactory, SaveRepository saveRepository, LeaderboardRepository leaderboardRepository) {
        this.gameFactory = Objects.requireNonNull(gameFactory);
        this.saveRepository = Objects.requireNonNull(saveRepository);
        this.leaderboardRepository = Objects.requireNonNull(leaderboardRepository);
    }

    /**
     * Starts a new game for the supplied player.
     * Any previously active game is replaced and the result-recording
     * state is reset for the new session.
     *
     * @param playerName name of the player starting the game
     * @return the newly created game engine
     */
    public GameEngine startNewGame(String playerName) {
        currentGame = gameFactory.createNewGame(playerName);
        resultRecorded = false;
        return currentGame;
    }

    /**
     * Restores the saved game and recreates its game engine.
     * The restored state is synchronized before it becomes the current
     * active game.
     *
     * @return game engine created for the restored state
     */
    public GameEngine loadSavedGame() {
        GameState restoredState = saveRepository.load();
        restoredState.getPlayer().getCurrentRoom().markAsVisited();
        restoredState.synchronizePhaseWithPositions();
        currentGame = gameFactory.createEngineFor(restoredState);
        resultRecorded = false;
        return currentGame;
    }

    /**
     * Returns the currently active game engine.
     *
     * @return current game engine
     * @throws IllegalStateException if no game is currently active
     */
    public GameEngine getCurrentGame() {
        if (currentGame == null) {
            throw new IllegalStateException("There is no active game");
        }
        return currentGame;
    }

    public boolean hasSavedGame() {
        return saveRepository.exists();
    }

    /**
     * Persists the state of the currently active game.
     * Completed games cannot be saved because their lifecycle has
     * already ended.
     *
     * @throws IllegalStateException if no game is active or if the
     *         current game has already finished
     */
    public void saveCurrentGame() {
        GameState state = getCurrentGame().getState();
        if (state.isFinished()) {
            throw new IllegalStateException("A finished game cannot be saved");
        }
        saveRepository.save(state);
    }

    /**
     * Completes persistence processing for the current game.
     * A completed victory is added to the leaderboard at most once.
     * The existing save file is then removed because the session
     * no longer represents a resumable game.
     */
    public void completeCurrentGame() {
        GameState state = getCurrentGame().getState();
        if (!state.isFinished()) {
            return;
        }
        if (state.getStatus() == GameStatus.WON && !resultRecorded) {
            LeaderboardEntry entry = new LeaderboardEntry(state.getPlayer().getName(), state.getTurnCount(), state.getPlayer().getStats().getLucidity(), System.currentTimeMillis());
            leaderboardRepository.add(entry);
            resultRecorded = true;
        }
        saveRepository.delete();
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboardRepository.findAll();
    }

    public void clearLeaderboard() {
        leaderboardRepository.clear();
    }
}
