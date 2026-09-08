package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Leaderboard screen.
 */
public class LeaderboardView extends VBox {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final VBox rows = new VBox();
    private final Button clearButton = new Button("CLEAR");
    private final Button backButton = new Button("BACK");

    public LeaderboardView() {
        setSpacing(20);
        setPadding(new Insets(35, 50, 35, 50));
        VintageTheme.screen(this);

        Label title = new Label("LEADERBOARD");
        VintageTheme.label(title, 32, Color.rgb(228, 215, 184), FontWeight.BOLD, false);

        VBox board = new VBox();
        board.setBorder(VintageTheme.border(Color.rgb(93, 82, 64), 1));
        board.setBackground(VintageTheme.solid(Color.rgb(23, 21, 16)));
        VBox.setVgrow(board, Priority.ALWAYS);
        board.getChildren().add(createHeader());
        ScrollPane scroll = new ScrollPane(rows);
        scroll.setFitToWidth(true);
        scroll.setBackground(Background.EMPTY);
        scroll.setBorder(null);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        board.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        HBox buttons = new HBox(12, clearButton, backButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VintageTheme.button(clearButton, VintageTheme.ButtonTone.DANGER);
        VintageTheme.button(backButton, VintageTheme.ButtonTone.PRIMARY);
        getChildren().addAll(title, board, buttons);
    }

    public void onClear(Runnable action) {
        clearButton.setOnAction(event -> action.run());
    }

    public void onBack(Runnable action) {
        backButton.setOnAction(event -> action.run());
    }

    public void showEntries(List<LeaderboardEntry> entries) {
        rows.getChildren().clear();
        if (entries.isEmpty()) {
            Label empty = new Label("No completed games yet.");
            empty.setPadding(new Insets(25));
            VintageTheme.label(empty, 14, VintageTheme.TEXT_MUTED, FontWeight.NORMAL, true);
            rows.getChildren().add(empty);
            return;
        }
        for (int index = 0; index < entries.size(); index++) {
            rows.getChildren().add(createRow(entries.get(index), index));
        }
    }

    private GridPane createHeader() {
        GridPane header = baseGrid();
        header.setPadding(new Insets(12));
        header.setBackground(VintageTheme.solid(Color.rgb(40, 35, 28)));
        addHeaderLabel(header, "Player", 0);
        addHeaderLabel(header, "Turns", 1);
        addHeaderLabel(header, "Lucidity", 2);
        addHeaderLabel(header, "Completed", 3);
        return header;
    }

    private GridPane createRow(LeaderboardEntry entry, int index) {
        GridPane row = baseGrid();
        row.setPadding(new Insets(11, 12, 11, 12));
        row.setBackground(VintageTheme.solid(index % 2 == 0 ? Color.rgb(25, 23, 18) : Color.rgb(33, 30, 24)));
        addValue(row, entry.playerName(), 0);
        addValue(row, Integer.toString(entry.turns()), 1);
        addValue(row, Integer.toString(entry.remainingLucidity()), 2);
        addValue(row, formatDate(entry.completedAtEpochMillis()), 3);
        return row;
    }

    private GridPane baseGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        ColumnConstraints player = new ColumnConstraints();
        player.setPercentWidth(35);
        ColumnConstraints turns = new ColumnConstraints();
        turns.setPercentWidth(15);
        ColumnConstraints lucidity = new ColumnConstraints();
        lucidity.setPercentWidth(15);
        ColumnConstraints completed = new ColumnConstraints();
        completed.setPercentWidth(35);
        grid.getColumnConstraints().addAll(player, turns, lucidity, completed);
        return grid;
    }

    private void addHeaderLabel(GridPane grid, String text, int column) {
        Label label = new Label(text);
        VintageTheme.label(label, 13, Color.rgb(221, 208, 179), FontWeight.BOLD, false);
        grid.add(label, column, 0);
    }

    private void addValue(GridPane grid, String text, int column) {
        Label label = new Label(text);
        VintageTheme.label(label, 13, Color.rgb(217, 206, 181), FontWeight.NORMAL, false);
        grid.add(label, column, 0);
    }

    private String formatDate(long timestamp) {
        return DATE_FORMAT.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()));
    }
}
