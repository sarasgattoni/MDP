package it.unicam.cs.mpgc.rpg125676.controller;

import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.persistence.PersistenceException;
import it.unicam.cs.mpgc.rpg125676.view.MainMenuView;
import it.unicam.cs.mpgc.rpg125676.view.SceneNavigator;
import it.unicam.cs.mpgc.rpg125676.view.support.GameDialogManager;
import it.unicam.cs.mpgc.rpg125676.view.support.GameNarrativeCatalog;
import javafx.application.Platform;

import java.util.Objects;

/**
 * Coordinates the main menu and the operations available before
 * entering a game.
 * The controller manages new-game creation, saved-game loading,
 * leaderboard navigation and application exit.
 */
public class MainMenuController {

    private final MainMenuView view;
    private final GameSession session;
    private final SceneNavigator navigator;
    private final GameDialogManager dialogs = new GameDialogManager(new GameNarrativeCatalog());

    /**
     * Creates a controller for the supplied main menu view.
     *
     * @param view main menu view controlled by this instance
     * @param session application game session
     * @param navigator navigator used to change application screens
     */
    public MainMenuController(MainMenuView view, GameSession session, SceneNavigator navigator) {
        this.view = Objects.requireNonNull(view);
        this.session = Objects.requireNonNull(session);
        this.navigator = Objects.requireNonNull(navigator);
    }

    /**
     * Initializes the main menu controls and updates the availability
     * of the continue operation according to the saved-game state.
     */
    public void initialize() {
        view.setContinueEnabled(session.hasSavedGame());
        view.onNewGame(this::startNewGame);
        view.onContinue(this::continueGame);
        view.onLeaderboard(navigator::showLeaderboard);
        view.onExit(Platform::exit);
    }

    /**
     * Requests a player name and starts a new game session.
     * The game screen is opened only after a valid player name has
     * been provided and the session has been successfully created.
     */
    private void startNewGame() {
        var result = dialogs.requestPlayerName();
        if (result.isEmpty()) {
            return;
        }

        String playerName = result.get().trim();
        if (playerName.isBlank()) {
            dialogs.showError("Unable to continue", "The player name cannot be empty.");
            return;
        }

        try {
            session.startNewGame(playerName);
            navigator.showGame();
        } catch (RuntimeException exception) {
            dialogs.showError("Unable to start the game", exception.getMessage());
        }
    }

    /**
     * Restores the saved game and opens the game screen.
     */
    private void continueGame() {
        try {
            session.loadSavedGame();
            navigator.showGame();
        } catch (PersistenceException exception) {
            dialogs.showError("Unable to continue", "The saved game could not be loaded.\n" + exception.getMessage());
        }
    }
}
