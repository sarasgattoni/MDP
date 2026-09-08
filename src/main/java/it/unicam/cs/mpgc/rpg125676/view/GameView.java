package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.view.component.ActionPanel;
import it.unicam.cs.mpgc.rpg125676.view.component.PlayerPanel;
import it.unicam.cs.mpgc.rpg125676.view.component.RoomPanel;
import it.unicam.cs.mpgc.rpg125676.view.component.StatusBar;
import it.unicam.cs.mpgc.rpg125676.view.support.GameNarrativeCatalog;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Main game screen.
 */
public class GameView extends BorderPane {

    private final Label turnValue = new Label();
    private final Label attentionValue = new Label();
    private final StatusBar attentionBar = new StatusBar(115);

    private final PlayerPanel playerPanel = new PlayerPanel();
    private final RoomPanel roomPanel = new RoomPanel();
    private final ActionPanel actionPanel = new ActionPanel();

    private final Button saveButton = new Button("SAVE GAME");
    private final Button saveAndMenuButton = new Button("SAVE & MENU");

    public GameView() {
        VintageTheme.screen(this);
        setTop(createHeader());
        setLeft(playerPanel);
        setCenter(roomPanel);
        setRight(actionPanel);
        setBottom(createFooter());
    }

    public PlayerPanel getPlayerPanel() {
        return playerPanel;
    }

    public RoomPanel getRoomPanel() {
        return roomPanel;
    }

    public ActionPanel getActionPanel() {
        return actionPanel;
    }

    public void onSave(Runnable action) {
        saveButton.setOnAction(event -> action.run());
    }

    public void onSaveAndMenu(Runnable action) {
        saveAndMenuButton.setOnAction(event -> action.run());
    }

    public void initializeHouse(GameState state) {
        playerPanel.setHouse(state.getHouse());
    }

    public void refresh(GameState state, GameNarrativeCatalog narrativeCatalog) {
        turnValue.setText(Integer.toString(state.getTurnCount()));

        int attention = state.getPresence().getAttention();
        attentionValue.setText(Integer.toString(attention));
        attentionBar.setProgress((double) attention / state.getSettings().presence().maxAttention());
        attentionBar.setFillColor(attention <= state.getSettings().presence().dormantMaxAttention() ? VintageTheme.ATTENTION_DORMANT : attention <= state.getSettings().presence().huntingMaxAttention() ? VintageTheme.ATTENTION_HUNTING : VintageTheme.ATTENTION_UNLEASHED);
        playerPanel.refresh(state);
        actionPanel.refresh(state);
        roomPanel.showRoom(state.getPlayer().getCurrentRoom(), narrativeCatalog.roomDescription(state.getPlayer().getCurrentRoom()));
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 18, 12, 18));
        VintageTheme.topPanel(header);
        Label title = new Label("THE HOUSE THAT LISTENS");
        VintageTheme.label(title, 22, Color.rgb(222, 211, 183), FontWeight.BOLD, false);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label turnLabel = new Label("TURN");
        VintageTheme.label(turnLabel, 11, Color.rgb(157, 145, 122), FontWeight.NORMAL, false);
        VintageTheme.label(turnValue, 15, Color.rgb(229, 216, 188), FontWeight.BOLD, false);
        Label attentionLabel = new Label("ATTENTION");
        VintageTheme.label(attentionLabel, 11, Color.rgb(178, 116, 104), FontWeight.BOLD, false);
        VintageTheme.label(attentionValue, 18, Color.rgb(197, 95, 84), FontWeight.BOLD, false);
        VintageTheme.button(saveAndMenuButton, VintageTheme.ButtonTone.SECONDARY);
        header.getChildren().addAll(title, spacer, turnLabel, turnValue, attentionLabel, attentionValue, attentionBar, saveAndMenuButton);
        return header;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(8, 18, 8, 18));
        VintageTheme.bottomPanel(footer);
        Label quote = new Label("Some doors should remain closed.");
        VintageTheme.label(quote, 11, Color.rgb(129, 118, 99), FontWeight.NORMAL, true);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VintageTheme.button(saveButton, VintageTheme.ButtonTone.SECONDARY);
        footer.getChildren().addAll(quote, spacer, saveButton);
        return footer;
    }
}
