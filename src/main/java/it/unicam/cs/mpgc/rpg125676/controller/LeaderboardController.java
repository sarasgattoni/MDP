package it.unicam.cs.mpgc.rpg125676.controller;

import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.view.LeaderboardView;
import it.unicam.cs.mpgc.rpg125676.view.SceneNavigator;
import it.unicam.cs.mpgc.rpg125676.view.support.GameDialogManager;
import it.unicam.cs.mpgc.rpg125676.view.support.GameNarrativeCatalog;

import java.util.Objects;

/**
 * Coordinates the leaderboard screen.
 * The controller retrieves leaderboard data from the current
 * game session, updates the view and handles leaderboard clearing
 * and navigation back to the main menu.
 */
public class LeaderboardController {

    private final LeaderboardView view;
    private final GameSession session;
    private final SceneNavigator navigator;
    private final GameDialogManager dialogs = new GameDialogManager(new GameNarrativeCatalog());

    /**
     * Creates a controller for the supplied leaderboard view.
     *
     * @param view leaderboard view controlled by this instance
     * @param session application game session providing leaderboard data
     * @param navigator navigator used to change application screens
     */
    public LeaderboardController(LeaderboardView view, GameSession session, SceneNavigator navigator) {
        this.view = Objects.requireNonNull(view);
        this.session = Objects.requireNonNull(session);
        this.navigator = Objects.requireNonNull(navigator);
    }

    /**
     * Initializes leaderboard controls and displays the currently
     * stored entries.
     */
    public void initialize() {
        view.onBack(navigator::showMainMenu);
        view.onClear(this::clearLeaderboard);
        refresh();
    }

    /**
     * Requests confirmation before clearing all leaderboard entries.
     * The displayed ranking is refreshed after a successful removal.
     */
    private void clearLeaderboard() {
        if (!dialogs.confirmClearLeaderboard()) {
            return;
        }
        session.clearLeaderboard();
        refresh();
    }

    private void refresh() {
        view.showEntries(session.getLeaderboard());
    }
}
