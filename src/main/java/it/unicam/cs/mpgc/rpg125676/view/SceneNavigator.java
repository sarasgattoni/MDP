package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.controller.GameController;
import it.unicam.cs.mpgc.rpg125676.controller.GameOverController;
import it.unicam.cs.mpgc.rpg125676.controller.LeaderboardController;
import it.unicam.cs.mpgc.rpg125676.controller.MainMenuController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Handles navigation between scenes.
 */
public class SceneNavigator {

    private static final double WINDOW_WIDTH = 1150;
    private static final double WINDOW_HEIGHT = 720;
    private final Stage stage;
    private final GameSession session;

    public SceneNavigator(Stage stage, GameSession session) {
        this.stage = Objects.requireNonNull(stage);
        this.session = Objects.requireNonNull(session);
        stage.setTitle("The House That Listens");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
    }

    public void showMainMenu() {
        MainMenuView view = new MainMenuView();
        new MainMenuController(view, session, this).initialize();
        show(view);
    }

    public void showGame() {
        GameView view = new GameView();
        new GameController(view, session, this).initialize();
        show(view);
    }

    public void showLeaderboard() {
        LeaderboardView view = new LeaderboardView();
        new LeaderboardController(view, session, this).initialize();
        show(view);
    }

    public void showGameOver() {
        GameOverView view = new GameOverView();
        new GameOverController(view, session, this).initialize();
        show(view);
    }

    private void show(Parent root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.centerOnScreen();
        stage.show();
    }
}
