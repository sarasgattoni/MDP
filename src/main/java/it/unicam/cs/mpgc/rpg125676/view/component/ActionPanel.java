package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/**
 * Displays the actions available to the player.
 */
public class ActionPanel extends VBox {

    private static final double PANEL_WIDTH = 215;

    private final ComboBox<Room> destinations = new ComboBox<>();
    private final VBox explorationBox = new VBox(8);
    private final VBox confrontationBox = new VBox(10);

    private final Button moveButton = createButton("MOVE", VintageTheme.ButtonTone.ACTION);
    private final Button searchButton = createButton("SEARCH", VintageTheme.ButtonTone.ACTION);
    private final Button listenButton = createButton("LISTEN", VintageTheme.ButtonTone.ACTION);
    private final Button catchBreathButton = createButton("CATCH BREATH", VintageTheme.ButtonTone.ACTION);
    private final Button placeDecoyButton = createButton("PLACE DECOY", VintageTheme.ButtonTone.ACTION);
    private final Button speakNameButton = createButton("SPEAK NAME", VintageTheme.ButtonTone.FINAL);
    private final Button faceButton = createButton("FACE PRESENCE", VintageTheme.ButtonTone.DANGER);
    private final Button hideButton = createButton("HIDE", VintageTheme.ButtonTone.ACTION);

    public ActionPanel() {
        configurePanel();
        buildExplorationBox();
        buildConfrontationBox();
        Label heading = new Label("ACTIONS");
        VintageTheme.label(heading, 20, Color.rgb(214, 197, 162), FontWeight.BOLD, false);
        getChildren().addAll(heading, explorationBox, confrontationBox);
    }

    public Room getSelectedDestination() {
        return destinations.getValue();
    }

    public void onMove(Runnable action) {
        moveButton.setOnAction(event -> action.run());
    }

    public void onSearch(Runnable action) {
        searchButton.setOnAction(event -> action.run());
    }

    public void onListen(Runnable action) {
        listenButton.setOnAction(event -> action.run());
    }

    public void onCatchBreath(Runnable action) {
        catchBreathButton.setOnAction(event -> action.run());
    }

    public void onPlaceDecoy(Runnable action) {
        placeDecoyButton.setOnAction(event -> action.run());
    }

    public void onSpeakName(Runnable action) {
        speakNameButton.setOnAction(event -> action.run());
    }

    public void onFacePresence(Runnable action) {
        faceButton.setOnAction(event -> action.run());
    }

    public void onHide(Runnable action) {
        hideButton.setOnAction(event -> action.run());
    }

    public void refresh(GameState state) {
        boolean confrontation = state.getPhase() == GamePhase.CONFRONTATION;
        showOnly(confrontation ? confrontationBox : explorationBox,
                confrontation ? explorationBox : confrontationBox);
        if (!confrontation) {
            refreshExplorationActions(state);
        }
    }

    private void configurePanel() {
        setMinWidth(PANEL_WIDTH);
        setPrefWidth(PANEL_WIDTH);
        setMaxWidth(PANEL_WIDTH);
        setSpacing(9);
        setPadding(new Insets(14));
        VintageTheme.sidePanel(this);
    }

    private void buildExplorationBox() {
        Label destinationLabel = new Label("Destination");
        VintageTheme.label(destinationLabel, 11, VintageTheme.TEXT_MUTED, FontWeight.BOLD, false);
        destinations.setPromptText("Choose a room");
        destinations.setMaxWidth(Double.MAX_VALUE);
        VintageTheme.comboBox(destinations);
        configureDestinationRendering();
        explorationBox.getChildren().addAll(
                destinationLabel,
                destinations,
                moveButton,
                searchButton,
                listenButton,
                catchBreathButton,
                placeDecoyButton,
                activateDecoyButton,
                speakNameButton
        );
    }

    private void buildConfrontationBox() {
        VintageTheme.confrontationPanel(confrontationBox);
        Label warning = new Label("THE PRESENCE IS HERE");
        warning.setWrapText(true);
        VintageTheme.label(warning, 17, Color.rgb(197, 109, 98), FontWeight.BOLD, false);
        Label description = new Label("There is nowhere to run.");
        description.setWrapText(true);
        VintageTheme.label(description, 12, Color.rgb(170, 130, 122), FontWeight.NORMAL, true);
        confrontationBox.getChildren().addAll(warning, description, faceButton, hideButton);
        confrontationBox.setVisible(false);
        confrontationBox.setManaged(false);
    }

    private void refreshExplorationActions(GameState state) {
        Room currentRoom = state.getPlayer().getCurrentRoom();
        refreshDestinations(state, currentRoom);
        searchButton.setDisable(!currentRoom.isSearchable() || currentRoom.isSearched());
        refreshListenButton(state);
        catchBreathButton.setDisable(!canCatchBreath(state));
        speakNameButton.setDisable(!canSpeakName(state, currentRoom));
        boolean hasPlacedDecoy = state.hasPlacedDecoy();
        placeDecoyButton.setDisable(hasPlacedDecoy || state.getPlayer().getDecoyCount() <= 0);
        boolean ringing = state.getPlacedDecoy().map(PlacedDecoy::isRinging).orElse(false);
        activateDecoyButton.setDisable(!hasPlacedDecoy || ringing);
        activateDecoyButton.setText(ringing ? "DECOY RINGING" : "ACTIVATE DECOY");
    }

    private void refreshListenButton(GameState state) {
        int cooldown = state.getListenCooldownRemaining();
        listenButton.setDisable(cooldown > 0);
        listenButton.setText(cooldown == 0 ? "LISTEN" : "LISTEN (" + cooldown + ")");
    }

    private boolean canCatchBreath(GameState state) {
        return state.getPresence().getAttention() == 0 && state.isRecoveryAvailable() && state.getPlayer().getStats().getLucidity() < state.getSettings().player().maxLucidity();
    }

    private boolean canSpeakName(GameState state, Room currentRoom) {
        return currentRoom.getRole() == RoomRole.FINAL && state.getPlayer().getMemoriesFound() >= state.getSettings().content().memoryCount();
    }

    private void refreshDestinations(GameState state, Room currentRoom) {
        Room previousSelection = destinations.getValue();
        destinations.getItems().setAll(state.getHouse().getAdjacentRooms(currentRoom));
        if (previousSelection != null && destinations.getItems().contains(previousSelection)) {
            destinations.setValue(previousSelection);
        } else {
            destinations.getSelectionModel().clearSelection();
        }
    }

    private void configureDestinationRendering() {
        destinations.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room == null ? "" : room.getName();
            }

            @Override
            public Room fromString(String text) {
                return null;
            }
        });

        destinations.setCellFactory(list -> new RoomCell());
        destinations.setButtonCell(new RoomCell());
    }

    private void showOnly(VBox visibleBox, VBox hiddenBox) {
        visibleBox.setVisible(true);
        visibleBox.setManaged(true);
        hiddenBox.setVisible(false);
        hiddenBox.setManaged(false);
    }

    private Button createButton(String text, VintageTheme.ButtonTone tone) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        VintageTheme.button(button, tone);
        return button;
    }
    private final Button activateDecoyButton = createButton("ACTIVATE DECOY", VintageTheme.ButtonTone.ACTION);

    public void onActivateDecoy(Runnable action) {activateDecoyButton.setOnAction(event -> action.run());}

    private static class RoomCell extends ListCell<Room> {

        private RoomCell() {
            setFont(Font.font("Georgia", FontWeight.NORMAL, FontPosture.REGULAR, 13));
            setTextFill(Color.rgb(223, 212, 187));
            setBackground(Background.EMPTY);
        }

        @Override
        protected void updateItem(Room room, boolean empty) {
            super.updateItem(room, empty);
            setText(empty || room == null ? null : room.getName());
        }
    }
}
