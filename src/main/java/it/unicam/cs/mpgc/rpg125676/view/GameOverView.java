package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameStatus;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Dedicated victory or defeat screen.
 */
public class GameOverView extends VBox {

    private final Label resultTitle = new Label();
    private final Label narrative = new Label();
    private final Label statistics = new Label();
    private final Button leaderboardButton = new Button("LEADERBOARD");
    private final Button menuButton = new Button("MAIN MENU");

    public GameOverView() {
        setAlignment(Pos.CENTER);
        setSpacing(18);
        setPadding(new Insets(50));
        VintageTheme.menuScreen(this);
        narrative.setWrapText(true);
        narrative.setMaxWidth(580);
        narrative.setAlignment(Pos.CENTER);
        statistics.setAlignment(Pos.CENTER);
        VintageTheme.label(narrative, 15, Color.rgb(190, 178, 151), FontWeight.NORMAL, true);
        VintageTheme.label(statistics, 14, Color.rgb(222, 210, 183), FontWeight.BOLD, false);
        VintageTheme.button(leaderboardButton, VintageTheme.ButtonTone.SECONDARY);
        VintageTheme.button(menuButton, VintageTheme.ButtonTone.PRIMARY);
        leaderboardButton.setPrefWidth(260);
        menuButton.setPrefWidth(260);
        getChildren().addAll(resultTitle, spacer(12), narrative, statistics, spacer(18), leaderboardButton, menuButton);
    }

    public void showResult(GameState state) {
        boolean won = state.getStatus() == GameStatus.WON;
        resultTitle.setText(won ? "THE HOUSE FALLS SILENT" : "THE HOUSE KEEPS YOU");
        VintageTheme.label(resultTitle, 34, won ? Color.rgb(220, 205, 169) : Color.rgb(207, 116, 105), FontWeight.BOLD, false);
        narrative.setText(won ? "You spoke Eleanor's name. For the first time in decades, the house stops listening." : "Your Lucidity fades. Somewhere in the darkness, the presence is still listening.");
        statistics.setText("Player: " + state.getPlayer().getName() + "    •    Turns: " + state.getTurnCount() + "    •    Lucidity: " + state.getPlayer().getStats().getLucidity());
    }

    public void onLeaderboard(Runnable action) {
        leaderboardButton.setOnAction(event -> action.run());
    }

    public void onMainMenu(Runnable action) {
        menuButton.setOnAction(event -> action.run());
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }
}
