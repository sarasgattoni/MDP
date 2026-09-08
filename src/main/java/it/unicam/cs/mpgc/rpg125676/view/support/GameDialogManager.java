package it.unicam.cs.mpgc.rpg125676.view.support;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.Optional;

/**
 * Centralizes the creation and presentation of application dialogs.
 */
public class GameDialogManager {

    private final GameNarrativeCatalog narrativeCatalog;

    public GameDialogManager(GameNarrativeCatalog narrativeCatalog) {
        this.narrativeCatalog = narrativeCatalog;
    }

    public Optional<String> requestPlayerName() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("New Game");
        ButtonType start = new ButtonType("START");
        dialog.getDialogPane().getButtonTypes().addAll(start, ButtonType.CANCEL);
        Label title = new Label("ENTER THE PLAYER'S NAME");
        VintageTheme.label(title, 17, Color.rgb(226, 210, 173), FontWeight.BOLD, false);
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setPrefWidth(320);
        nameField.setFont(javafx.scene.text.Font.font("Georgia", 13));
        VBox content = new VBox(12, title, nameField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        styleDialogPane(dialog.getDialogPane());
        styleDialogButtons(dialog.getDialogPane(), start);
        dialog.setResultConverter(button -> button == start ? nameField.getText().trim() : null);
        return dialog.showAndWait();
    }

    /**
     * Requests the attribute to improve after recovering a memory.
     *
     * @param memoryNumber number of the recovered memory
     * @return selected player attribute
     */
    public PlayerAttribute requestMemoryAttribute(int memoryNumber) {
        ButtonType composure = new ButtonType("COMPOSURE");
        ButtonType caution = new ButtonType("CAUTION");
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, "", composure, caution);
        dialog.setTitle("Recovered Memory");
        dialog.setHeaderText(null);
        Label title = new Label(narrativeCatalog.memoryTitle(memoryNumber));
        VintageTheme.label(title, 18, Color.rgb(226, 210, 173), FontWeight.BOLD, false);
        Label story = new Label(narrativeCatalog.memoryText(memoryNumber));
        story.setWrapText(true);
        story.setMaxWidth(520);
        story.setPadding(new Insets(10, 0, 5, 0));
        VintageTheme.label(story, 14, Color.rgb(215, 201, 168), FontWeight.NORMAL, false);
        VBox content = new VBox(8, title, story);
        dialog.getDialogPane().setContent(content);
        styleDialogPane(dialog.getDialogPane());
        styleDialogButtons(dialog.getDialogPane(), composure, caution);

        while (true) {
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty()) {
                continue;
            }
            return result.get() == composure ? PlayerAttribute.COMPOSURE : PlayerAttribute.CAUTION;
        }
    }

    public boolean confirmClearLeaderboard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Clear Leaderboard");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(dialogContent("CLEAR LEADERBOARD", "Delete every leaderboard entry?"));
        styleDialogPane(alert.getDialogPane());
        styleDialogButtons(alert.getDialogPane(), ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public void showInformation(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    public void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Action unavailable", message);
    }

    public void showError(String title, String message) {
        show(Alert.AlertType.ERROR, title, message == null ? "An unexpected error occurred." : message);
    }

    private void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, "", ButtonType.OK);
        alert.setTitle("The House That Listens");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(dialogContent(title, message));
        styleDialogPane(alert.getDialogPane());
        styleDialogButtons(alert.getDialogPane(), ButtonType.OK);
        alert.showAndWait();
    }

    private VBox dialogContent(String titleText, String message) {
        Label title = new Label(titleText.toUpperCase());
        VintageTheme.label(title, 17, Color.rgb(226, 210, 173), FontWeight.BOLD, false);
        Label body = new Label(message);
        body.setWrapText(true);
        body.setMaxWidth(520);
        VintageTheme.label(body, 13, Color.rgb(214, 201, 170), FontWeight.NORMAL, false);
        VBox box = new VBox(10, title, body);
        box.setPadding(new Insets(8));
        return box;
    }

    private void styleDialogPane(DialogPane pane) {
        pane.setPrefWidth(620);
        pane.setBackground(VintageTheme.solid(Color.rgb(27, 24, 19)));
        pane.setBorder(VintageTheme.border(Color.rgb(120, 102, 76), 1));
    }

    private void styleDialogButtons(DialogPane pane, ButtonType... buttonTypes) {
        for (ButtonType buttonType : buttonTypes) {
            Node node = pane.lookupButton(buttonType);
            if (node instanceof Button button) {
                VintageTheme.button(button, VintageTheme.ButtonTone.SECONDARY);
            }
        }
    }
}
