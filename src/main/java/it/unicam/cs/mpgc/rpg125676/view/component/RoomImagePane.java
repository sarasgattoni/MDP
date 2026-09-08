package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.net.URL;

/**
 * Displays the photograph associated with the current room.
 * This component is responsible for loading room images,
 * resizing and cropping them, showing the fallback placeholder
 * and applying the confrontation visual effect.
 */
public class RoomImagePane extends StackPane {

    private static final double IMAGE_HEIGHT = 360;
    private static final double IMAGE_PADDING = 10;

    private static final String IMAGE_DIRECTORY = "/images/rooms/";

    private static final String[] IMAGE_EXTENSIONS = {
            ".png",
            ".jpg",
            ".jpeg"
    };

    private final ImageView imageView = new ImageView();
    private final VBox imagePlaceholder = new VBox(8);
    private final Label imagePlaceholderTitle = new Label("ROOM IMAGE");
    private boolean confrontationVisible;

    public RoomImagePane() {
        configurePane();
        configureImageView();
        configurePlaceholder();
        configureResizeListeners();
        getChildren().addAll(imageView, imagePlaceholder);
    }

    /**
     * Displays the photograph associated with the given room.
     * If no photograph exists, the fallback placeholder is shown.
     *
     * @param room room to display
     */
    public void showRoom(Room room) {
        URL resource = findRoomImage(room);
        if (resource == null) {
            showPlaceholder(room);
            return;
        }
        Image image = new Image(resource.toExternalForm());

        imageView.setViewport(null);
        imageView.setImage(image);
        imageView.setEffect(createVintageEffect());
        imagePlaceholder.setVisible(false);
        imagePlaceholder.setManaged(false);
        layoutImage();
    }

    /**
     * Updates the frame appearance during a confrontation.
     *
     * @param confrontation true if the Presence is in the room
     */
    public void setConfrontation(boolean confrontation) {
        VintageTheme.roomFrame(this, confrontation);
        if (confrontation && !confrontationVisible) {
            playConfrontationAnimation();
        }
        confrontationVisible = confrontation;
    }

    private void configurePane() {
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        setMinHeight(IMAGE_HEIGHT);
        setPrefHeight(IMAGE_HEIGHT);
        setMaxHeight(IMAGE_HEIGHT);
        setAlignment(Pos.CENTER);
        VintageTheme.roomFrame(this, false);
    }

    private void configureImageView() {
        imageView.setManaged(false);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setCache(true);
    }

    private void configurePlaceholder() {
        imagePlaceholder.setAlignment(Pos.CENTER);
        imagePlaceholder.setMouseTransparent(true);
        VintageTheme.label(imagePlaceholderTitle, 24, Color.rgb(121, 109, 88), FontWeight.BOLD, false);
        Label unavailable = new Label("Photographic evidence unavailable");
        unavailable.setWrapText(true);
        VintageTheme.label(unavailable, 12, Color.rgb(98, 89, 74), FontWeight.NORMAL, true);
        imagePlaceholder.getChildren().addAll(imagePlaceholderTitle, unavailable);
    }

    private void configureResizeListeners() {
        widthProperty().addListener((observable, oldValue, newValue) -> layoutImage());
        heightProperty().addListener((observable, oldValue, newValue) -> layoutImage());
        imageView.imageProperty().addListener((observable, oldImage, newImage) -> layoutImage());
    }

    private URL findRoomImage(Room room) {
        String basePath = IMAGE_DIRECTORY + room.getId();
        for (String extension : IMAGE_EXTENSIONS) {
            URL resource = RoomImagePane.class.getResource(basePath + extension);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    private void showPlaceholder(Room room) {
        imageView.setImage(null);
        imageView.setViewport(null);
        imageView.setEffect(null);
        imagePlaceholderTitle.setText(room.getName());
        imagePlaceholder.setVisible(true);
        imagePlaceholder.setManaged(true);
    }


    private void layoutImage() {
        Image image = imageView.getImage();
        if (image == null) {
            return;
        }
        double availableWidth = Math.max(0, getWidth() - IMAGE_PADDING * 2);
        double availableHeight = Math.max(0, getHeight() - IMAGE_PADDING * 2);
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }
        Rectangle2D viewport = calculateViewport(imageWidth, imageHeight, availableWidth, availableHeight);
        imageView.setViewport(viewport);
        imageView.setFitWidth(availableWidth);
        imageView.setFitHeight(availableHeight);
        imageView.relocate(IMAGE_PADDING, IMAGE_PADDING);
    }

    private Rectangle2D calculateViewport(double imageWidth, double imageHeight, double frameWidth, double frameHeight) {
        double frameRatio = frameWidth / frameHeight;
        double imageRatio = imageWidth / imageHeight;
        if (imageRatio > frameRatio) {
            double croppedWidth = imageHeight * frameRatio;
            double x = (imageWidth - croppedWidth) / 2.0;
            return new Rectangle2D(x, 0, croppedWidth, imageHeight);
        }

        double croppedHeight = imageWidth / frameRatio;
        double y = (imageHeight - croppedHeight) / 2.0;
        return new Rectangle2D(0, y, imageWidth, croppedHeight);
    }

    private SepiaTone createVintageEffect() {
        ColorAdjust adjustment = new ColorAdjust();
        adjustment.setSaturation(-0.40);
        adjustment.setContrast(0.10);
        adjustment.setBrightness(-0.08);
        SepiaTone sepia = new SepiaTone(0.30);
        sepia.setInput(adjustment);
        return sepia;
    }

    private void playConfrontationAnimation() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(180), this);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
}