package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.view.HouseMapView;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Displays player statistics, inventory summary and the house map.
 */
public class PlayerPanel extends VBox {

    private final Label playerName = new Label();
    private final Label lucidityValue = new Label();
    private final Label composureValue = new Label();
    private final Label cautionValue = new Label();
    private final Label memoriesValue = new Label();
    private final Label decoysValue = new Label();
    private final Label decoyStatusValue = new Label();
    private final Label phaseValue = new Label();

    private final StatusBar lucidityBar = new StatusBar(190);
    private final HouseMapView houseMapView = new HouseMapView();

    public PlayerPanel() {
        setMinWidth(225);
        setPrefWidth(225);
        setMaxWidth(225);
        setSpacing(9);
        setPadding(new Insets(14));
        VintageTheme.sidePanel(this);

        Label heading = label("PLAYER", 20, Color.rgb(214, 197, 162), FontWeight.BOLD, false);
        VintageTheme.label(playerName, 23, Color.rgb(227, 213, 183), FontWeight.NORMAL, true);

        GridPane stats = createStatsGrid();
        HBox counters = createCounters();

        Label decoyTitle = label("DECOY", 11, VintageTheme.TEXT_MUTED, FontWeight.BOLD, false);
        VintageTheme.label(decoyStatusValue, 12, Color.rgb(170, 160, 139), FontWeight.NORMAL, false);
        decoyStatusValue.setWrapText(true);

        Label mapTitle = label("HOUSE MAP", 11, VintageTheme.TEXT_MUTED, FontWeight.BOLD, false);
        houseMapView.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox phase = new HBox(10);
        phase.setAlignment(Pos.CENTER_LEFT);
        Label phaseTitle = label("PHASE", 11, VintageTheme.TEXT_MUTED, FontWeight.BOLD, false);
        VintageTheme.label(phaseValue, 13, Color.rgb(211, 195, 163), FontWeight.BOLD, false);
        phase.getChildren().addAll(phaseTitle, phaseValue);

        getChildren().addAll(
                heading,
                playerName,
                divider(),
                stats,
                lucidityBar,
                divider(),
                counters,
                decoyTitle,
                decoyStatusValue,
                divider(),
                mapTitle,
                houseMapView,
                spacer,
                phase
        );
    }

    public void setHouse(House house) {
        houseMapView.setHouse(house);
    }

    public HouseMapView getHouseMapView() {
        return houseMapView;
    }

    public void refresh(GameState state) {
        var player = state.getPlayer();
        var stats = player.getStats();

        playerName.setText(player.getName());
        lucidityValue.setText(Integer.toString(stats.getLucidity()));
        composureValue.setText(Integer.toString(stats.getComposure()));
        cautionValue.setText(Integer.toString(stats.getCaution()));
        memoriesValue.setText(player.getMemoriesFound() + "/" + state.getSettings().content().memoryCount());
        decoysValue.setText(Integer.toString(player.getDecoyCount()));
        phaseValue.setText(state.getPhase().name().replace('_', ' '));
        decoyStatusValue.setText(
                state.getPlacedDecoy()
                        .map(decoy -> {
                            String roomName = decoy.getRoom().getName();

                            if (!decoy.isRinging()) {
                                return "Ready in " + roomName;
                            }

                            if (!decoy.hasPresenceReached()) {
                                return "Ringing in " + roomName
                                        + " — attracting Presence";
                            }

                            return "Ringing in " + roomName
                                    + " — holding Presence ("
                                    + decoy.getRemainingHoldTurns()
                                    + " turn(s))";
                        })
                        .orElse("None")
        );

        double progress = (double) stats.getLucidity() / state.getSettings().player().maxLucidity();
        lucidityBar.setProgress(progress);
        lucidityBar.setFillColor(progress > 0.6 ? VintageTheme.LUCIDITY_GOOD : progress > 0.3 ? VintageTheme.LUCIDITY_WARNING : VintageTheme.LUCIDITY_CRITICAL);

        houseMapView.setCurrentRoom(player.getCurrentRoom());
        houseMapView.setDecoyRoom(
                state.getPlacedDecoy()
                        .map(decoy -> decoy.getRoom())
                        .orElse(null)
        );
    }

    public void setComposureTooltip(String text) {
        composureValue.setTooltip(new Tooltip(text));
    }

    public void setCautionTooltip(String text) {
        cautionValue.setTooltip(new Tooltip(text));
    }

    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(7);

        addStat(grid, 0, "Lucidity", lucidityValue);
        addStat(grid, 1, "Composure", composureValue);
        addStat(grid, 2, "Caution", cautionValue);
        return grid;
    }

    private void addStat(GridPane grid, int row, String text, Label value) {
        Label name = label(text, 13, Color.rgb(169, 157, 134), FontWeight.NORMAL, false);
        VintageTheme.label(value, 17, VintageTheme.TEXT_BRIGHT, FontWeight.BOLD, false);
        grid.add(name, 0, row);
        grid.add(value, 1, row);
    }

    private HBox createCounters() {
        HBox counters = new HBox(24);
        counters.getChildren().addAll(
                createCounter("MEMORIES", memoriesValue),
                createCounter("DECOYS", decoysValue)
        );
        return counters;
    }

    private VBox createCounter(String title, Label value) {
        VBox box = new VBox(3);
        Label titleLabel = label(title, 11, VintageTheme.TEXT_MUTED, FontWeight.BOLD, false);
        VintageTheme.label(value, 19, Color.rgb(227, 212, 179), FontWeight.BOLD, false);
        box.getChildren().addAll(titleLabel, value);
        return box;
    }

    private Label label(String text, double size, Color color, FontWeight weight, boolean italic) {
        Label label = new Label(text);
        VintageTheme.label(label, size, color, weight, italic);
        return label;
    }

    private Region divider() {
        Region line = new Region();
        VintageTheme.divider(line);
        return line;
    }
}
