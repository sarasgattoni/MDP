package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Compact schematic representation of the house.
 */
public class HouseMapView extends Pane {

    private static final double HORIZONTAL_PADDING = 12;
    private static final double VERTICAL_PADDING = 12;
    private static final double ROOM_WIDTH = 34;
    private static final double ROOM_HEIGHT = 22;
    private static final double MARKER_SIZE = 15;

    private static final Map<String, RoomVisual> ROOM_VISUALS = Map.ofEntries(
            room("bathroom", 0.06, 0.10, "BTH"),
            room("parents_bedroom", 0.33, 0.10, "PBR"),
            room("child_bedroom", 0.63, 0.10, "CBR"),
            room("attic", 0.92, 0.10, "ATT"),
            room("locked_room", 0.32, 0.29, "LCK"),
            room("stairs", 0.50, 0.43, "STR"),
            room("living_room", 0.06, 0.69, "LIV"),
            room("study", 0.30, 0.69, "STD"),
            room("entrance", 0.06, 0.90, "ENT"),
            room("hallway", 0.50, 0.80, "HAL"),
            room("kitchen", 0.72, 0.80, "KIT"),
            room("pantry", 0.93, 0.80, "PAN")
    );

    private House house;
    private Room currentRoom;
    private Room visiblePresenceRoom;
    private Room activeDecoyRoom;

    public HouseMapView() {
        setMinHeight(190);
        setPrefHeight(205);
        setMaxHeight(215);
        setBackground(VintageTheme.solid(Color.rgb(23, 21, 16)));
        setBorder(VintageTheme.border(Color.rgb(102, 90, 69), 1));
        setEffect(new InnerShadow(8, Color.rgb(0, 0, 0, 0.65)));
        widthProperty().addListener((observable, oldValue, newValue) -> render());
        heightProperty().addListener((observable, oldValue, newValue) -> render());
    }

    public void setHouse(House house) {
        this.house = Objects.requireNonNull(house);
        render();
    }

    public void setCurrentRoom(Room room) {
        currentRoom = Objects.requireNonNull(room);
        render();
    }

    public void setActiveDecoyRoom(Room room) {
        activeDecoyRoom = room;
        render();
    }

    public void showPresence(Room room) {
        visiblePresenceRoom = Objects.requireNonNull(room);
        render();
    }

    public void hidePresence() {
        visiblePresenceRoom = null;
        render();
    }

    private void render() {
        getChildren().clear();
        if (house == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        drawConnections();
        drawRooms();
        drawMarkers();
    }

    private void drawConnections() {
        Set<String> drawnConnections = new HashSet<>();
        for (Room room : house.getRooms()) {
            RoomVisual first = visualFor(room);
            if (first == null) {
                continue;
            }
            for (Room adjacent : house.getAdjacentRooms(room)) {
                RoomVisual second = visualFor(adjacent);
                if (second == null || !drawnConnections.add(connectionKey(room, adjacent))) {
                    continue;
                }
                Line line = new Line(
                        toX(first.position()), toY(first.position()),
                        toX(second.position()), toY(second.position())
                );
                line.setStroke(Color.rgb(108, 96, 75));
                line.setStrokeWidth(1);
                line.setOpacity(0.75);
                getChildren().add(line);
            }
        }
    }

    private void drawRooms() {
        for (Room room : house.getRooms()) {
            RoomVisual visual = visualFor(room);
            if (visual == null) {
                continue;
            }
            Label node = createRoomNode(room, visual.shortName());
            node.setLayoutX(toX(visual.position()) - ROOM_WIDTH / 2);
            node.setLayoutY(toY(visual.position()) - ROOM_HEIGHT / 2);
            getChildren().add(node);
        }
    }

    private Label createRoomNode(Room room, String shortName) {
        Label label = new Label(shortName);
        label.setPrefSize(ROOM_WIDTH, ROOM_HEIGHT);
        label.setMinSize(ROOM_WIDTH, ROOM_HEIGHT);
        label.setMaxSize(ROOM_WIDTH, ROOM_HEIGHT);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Georgia", FontWeight.BOLD, 7));
        label.setTextFill(Color.rgb(197, 182, 150));
        label.setBackground(VintageTheme.solid(Color.rgb(41, 36, 28)));
        label.setBorder(VintageTheme.border(Color.rgb(112, 98, 73), 1));

        if (!room.isVisited()) {
            label.setOpacity(0.38);
        }
        if (room.equals(currentRoom)) {
            highlightCurrentRoom(label);
        }
        Tooltip.install(label, new Tooltip(room.getName()));
        return label;
    }

    private void highlightCurrentRoom(Label label) {
        label.setOpacity(1);
        label.setFont(Font.font("Georgia", FontWeight.BOLD, 8));
        label.setTextFill(Color.rgb(255, 240, 201));
        label.setBackground(VintageTheme.solid(Color.rgb(97, 64, 53)));
        label.setBorder(VintageTheme.border(Color.rgb(201, 160, 120), 2));
        label.setEffect(new DropShadow(7, Color.rgb(181, 112, 76, 0.75)));
    }

    private void drawMarkers() {
        if (activeDecoyRoom != null) {
            drawMarker(activeDecoyRoom, "D", Color.rgb(142, 112, 65), 10, -16, false);
        }
        if (visiblePresenceRoom != null) {
            drawMarker(visiblePresenceRoom, "P", Color.rgb(141, 57, 53), -18, -16, true);
        }
    }

    private void drawMarker(Room room, String text, Color background,
                            double offsetX, double offsetY, boolean danger) {
        RoomVisual visual = visualFor(room);
        if (visual == null) {
            return;
        }

        Label marker = new Label(text);
        marker.setAlignment(Pos.CENTER);
        marker.setMinSize(MARKER_SIZE, MARKER_SIZE);
        marker.setPrefSize(MARKER_SIZE, MARKER_SIZE);
        marker.setMaxSize(MARKER_SIZE, MARKER_SIZE);
        marker.setFont(Font.font("Georgia", FontWeight.BOLD, 8));
        marker.setTextFill(danger ? Color.rgb(255, 224, 218) : Color.rgb(255, 240, 197));
        marker.setBackground(new Background(new BackgroundFill(
                background, new CornerRadii(MARKER_SIZE), Insets.EMPTY
        )));
        if (danger) {
            marker.setEffect(new DropShadow(7, Color.rgb(170, 52, 45, 0.8)));
        }
        marker.setLayoutX(toX(visual.position()) + offsetX);
        marker.setLayoutY(toY(visual.position()) + offsetY);
        getChildren().add(marker);
    }

    private RoomVisual visualFor(Room room) {
        return ROOM_VISUALS.get(room.getId());
    }

    private String connectionKey(Room first, Room second) {
        return first.getId().compareTo(second.getId()) < 0
                ? first.getId() + "-" + second.getId()
                : second.getId() + "-" + first.getId();
    }

    private double toX(Point2D position) {
        return HORIZONTAL_PADDING + position.getX() * (getWidth() - HORIZONTAL_PADDING * 2);
    }

    private double toY(Point2D position) {
        return VERTICAL_PADDING + position.getY() * (getHeight() - VERTICAL_PADDING * 2);
    }

    private static Map.Entry<String, RoomVisual> room(String id, double x, double y, String shortName) {
        return Map.entry(id, new RoomVisual(new Point2D(x, y), shortName));
    }

    private record RoomVisual(Point2D position, String shortName) {
    }
}
