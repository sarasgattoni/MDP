package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Displays the current room and coordinates the visual
 * components related to room presentation.
 */
public class RoomPanel extends VBox {
    private final Label roomName = new Label();
    private final Label roomDescription = new Label();
    private final TurnFeedbackPane turnFeedbackPane = new TurnFeedbackPane();
    private final RoomImagePane roomImagePane = new RoomImagePane();
    private final FieldNotesPane fieldNotesPane = new FieldNotesPane();
    public RoomPanel() {
        configurePanel();
        configureRoomInformation();
        assembleLayout();
    }

    /**
     * Updates all visual information related to the current room.
     *
     * @param room current room
     * @param description narrative description
     */
    public void showRoom(Room room, String description) {
        roomName.setText(room.getName());
        roomDescription.setText(description);
        roomImagePane.showRoom(room);
    }

    /**
     * Initializes the field notes when a new game is started
     * or an existing game is loaded.
     *
     * @param newGame true for a newly started game
     */
    public void initializeLog(boolean newGame) {
        fieldNotesPane.initializeLog(newGame);
    }

    /**
     * Adds a message to the field notes.
     *
     * @param message message to display
     */
    public void appendEvent(String message) {
        fieldNotesPane.appendEvent(message);
    }

    public void showTurnFeedback(String mainText, String secondaryText, boolean success) {
        turnFeedbackPane.showTurnFeedback(mainText, secondaryText, success);
    }

    /**
     * Updates room visuals according to the confrontation state.
     *
     * @param confrontation true when the Presence is in the room
     */
    public void setConfrontation(boolean confrontation) {
        turnFeedbackPane.setConfrontation(confrontation);
        roomImagePane.setConfrontation(confrontation);
    }

    private void configurePanel() {
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        setSpacing(8);
        setPadding(new Insets(14, 18, 14, 18));
        VintageTheme.centerPanel(this);
        VBox.setVgrow(roomImagePane, Priority.ALWAYS);
    }

    private void configureRoomInformation() {
        VintageTheme.label(roomName, 30, Color.rgb(228, 215, 184), FontWeight.BOLD, false);
        VintageTheme.label(roomDescription, 12, Color.rgb(143, 132, 111), FontWeight.NORMAL, true);
        roomName.setMinWidth(0);
        roomDescription.setMinWidth(0);
        roomDescription.setMaxWidth(Double.MAX_VALUE);
        roomDescription.setWrapText(true);
    }

    private void assembleLayout() {
        getChildren().addAll(createCaption("CURRENT LOCATION"), roomName, roomDescription, turnFeedbackPane, roomImagePane, fieldNotesPane);
    }

    private Label createCaption(String text) {
        Label label = new Label(text);
        VintageTheme.label(label, 11, Color.rgb(139, 128, 108), FontWeight.BOLD, false);
        return label;
    }
}