package it.unicam.cs.mpgc.rpg125676.controller;

import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.view.GameOverView;
import it.unicam.cs.mpgc.rpg125676.view.SceneNavigator;

import java.util.Objects;

/**
 * Coordinates the screen displayed after a game has finished.
 * The controller presents the final game state and connects the
 * available navigation options to the leaderboard and main menu.
 */
public class GameOverController {

    private final GameOverView view;
    private final GameSession session;
    private final SceneNavigator navigator;

    /**
     * Creates a controller for the supplied game-over view.
     *
     * @param view final result view controlled by this instance
     * @param session application session containing the completed game
     * @param navigator navigator used to change application screens
     */
    public GameOverController(GameOverView view, GameSession session, SceneNavigator navigator) {
        this.view = Objects.requireNonNull(view);
        this.session = Objects.requireNonNull(session);
        this.navigator = Objects.requireNonNull(navigator);
    }

    /**
     * Displays the result of the completed game and connects the
     * navigation controls of the game-over screen.
     */
    public void initialize() {
        view.showResult(session.getCurrentGame().getState());
        view.onLeaderboard(navigator::showLeaderboard);
        view.onMainMenu(navigator::showMainMenu);
    }
}
