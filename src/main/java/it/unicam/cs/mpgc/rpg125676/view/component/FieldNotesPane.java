package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Displays the chronological narrative log of the current game.
 */
public class FieldNotesPane extends VBox {

    private final TextFlow eventLog = new TextFlow();
    private final ScrollPane eventScroll = new ScrollPane(eventLog);

    public FieldNotesPane() {
        setSpacing(4);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        configureEventLog();
        getChildren().addAll(createCaption(), eventScroll);
    }

    /**
     * Initializes the log when a game starts or is loaded.
     *
     * @param newGame whether the current game has just started
     */
    public void initializeLog(boolean newGame) {
        eventLog.getChildren().clear();

        if (newGame) {
            appendEvent("You step into the abandoned house.\nSomething inside is listening." );
        } else {
            appendEvent("You return to the house.\nIt remembers you." );
        }
    }

    /**
     * Adds a message to the bottom of the field notes.
     *
     * @param message message to display
     */
    public void appendEvent(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        Text text = new Text(message + System.lineSeparator() + System.lineSeparator());
        text.setFont(Font.font("Georgia", 13));
        text.setFill(VintageTheme.PAPER_TEXT);
        eventLog.getChildren().add(text);
        scrollToLatestEntry();
    }

    private void configureEventLog() {
        eventLog.setPadding(new Insets(9));
        eventLog.setLineSpacing(3);
        eventLog.setBackground(VintageTheme.solid(VintageTheme.PAPER));
        eventScroll.setMinWidth(0);
        eventScroll.setMaxWidth(Double.MAX_VALUE);
        eventScroll.setMinHeight(110);
        eventScroll.setPrefHeight(130);
        eventScroll.setMaxHeight(150);
        eventScroll.setFitToWidth(true);
        eventScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        eventScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        eventScroll.setBackground(VintageTheme.solid(VintageTheme.PAPER));
        eventScroll.setBorder(VintageTheme.border(Color.rgb(118, 104, 78), 2));
    }

    private void scrollToLatestEntry() {
        eventScroll.applyCss();
        eventScroll.layout();
        eventScroll.setVvalue(1.0);
    }

    private Label createCaption() {
        Label label = new Label("FIELD NOTES");
        VintageTheme.label(label, 11, Color.rgb(139, 128, 108), FontWeight.BOLD, false);
        return label;
    }
}