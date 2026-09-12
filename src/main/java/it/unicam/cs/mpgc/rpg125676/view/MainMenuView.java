package it.unicam.cs.mpgc.rpg125676.view;

import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.net.URL;

/**
 * Main menu.
 */
public class MainMenuView extends StackPane {

    private final Button newGameButton = new Button("NEW GAME");
    private final Button continueButton = new Button("CONTINUE");
    private final Button leaderboardButton = new Button("LEADERBOARD");
    private final Button exitButton = new Button("EXIT");

    public MainMenuView() {
        VintageTheme.menuScreen(this);
        configureBackgroundImage();
        getChildren().add(createCard());
    }

    public void setContinueEnabled(boolean enabled) {
        continueButton.setDisable(!enabled);
    }

    public void onNewGame(Runnable action) {
        newGameButton.setOnAction(event -> action.run());
    }

    public void onContinue(Runnable action) {
        continueButton.setOnAction(event -> action.run());
    }

    public void onLeaderboard(Runnable action) {
        leaderboardButton.setOnAction(event -> action.run());
    }

    public void onExit(Runnable action) {
        exitButton.setOnAction(event -> action.run());
    }

    private VBox createCard() {
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);
        card.setMaxHeight(650);
        card.setPadding(new Insets(45, 55, 45, 55));
        VintageTheme.card(card);
        Label titleOne = new Label("THE HOUSE");
        Label titleTwo = new Label("THAT LISTENS");
        VintageTheme.label(titleOne, 40, Color.rgb(222, 211, 183), FontWeight.BOLD, false);
        VintageTheme.label(titleTwo, 40, Color.rgb(222, 211, 183), FontWeight.BOLD, false);
        Label caption = new Label("—  THE WALLS REMEMBER.  —");
        VintageTheme.label(caption, 11, Color.rgb(139, 128, 108), FontWeight.BOLD, false);
        Label subtitle = new Label("Every sound draws it closer.");
        VintageTheme.label(subtitle, 15, Color.rgb(159, 149, 128), FontWeight.NORMAL, true);
        configureMenuButton(newGameButton, VintageTheme.ButtonTone.PRIMARY);
        configureMenuButton(continueButton, VintageTheme.ButtonTone.SECONDARY);
        configureMenuButton(leaderboardButton, VintageTheme.ButtonTone.SECONDARY);
        configureMenuButton(exitButton, VintageTheme.ButtonTone.DANGER);
        card.getChildren().addAll(titleOne, titleTwo, caption, spacer(20), subtitle, spacer(25), newGameButton, continueButton, leaderboardButton, exitButton, spacer(15));
        return card;
    }

    private void configureMenuButton(Button button, VintageTheme.ButtonTone tone) {
        button.setPrefWidth(280);
        VintageTheme.button(button, tone);
    }

    private Region spacer(double height) {
        Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }

    private void configureBackgroundImage() {
        URL resource = findMenuBackground();
        if (resource == null) {
            return;
        }

        Image image = new Image(resource.toExternalForm());
        ImageView background = new ImageView(image);

        background.setPreserveRatio(false);
        background.setSmooth(true);
        background.fitWidthProperty().bind(widthProperty());
        background.fitHeightProperty().bind(heightProperty());

        ColorAdjust adjustment = new ColorAdjust();
        adjustment.setSaturation(-0.25);
        adjustment.setBrightness(-0.05);
        adjustment.setContrast(0.08);

        SepiaTone sepia = new SepiaTone(0.25);
        sepia.setInput(adjustment);
        background.setEffect(sepia);

        Region overlay = new Region();
        overlay.setBackground(VintageTheme.solid(Color.rgb(0, 0, 0, 0.25)));
        overlay.maxWidthProperty().bind(widthProperty());
        overlay.maxHeightProperty().bind(heightProperty());

        getChildren().addAll(background, overlay);
    }

    private URL findMenuBackground() {
        String[] extensions = {".jpg", ".jpeg", ".png"};
        for (String extension : extensions) {
            URL resource = MainMenuView.class.getResource("/images/menu/villa" + extension);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }
}
