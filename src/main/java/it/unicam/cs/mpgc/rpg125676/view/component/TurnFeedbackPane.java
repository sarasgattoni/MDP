package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Displays temporary action feedback and confrontation warnings.
 */
public class TurnFeedbackPane extends VBox {
    private final VBox feedbackBox = new VBox(2);
    private final Label feedbackMain = new Label();
    private final Label feedbackSecondary = new Label();
    private final Label confrontationBanner = new Label("THE PRESENCE IS HERE");
    private PauseTransition feedbackHideTransition;

    public TurnFeedbackPane() {
        setSpacing(8);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        configureFeedback();
        configureConfrontationBanner();
        getChildren().addAll(feedbackBox, confrontationBanner);
    }

    /**
     * Displays feedback for the most recently resolved action.
     *
     * @param mainText primary feedback
     * @param secondaryText additional information
     * @param success whether the action succeeded
     */
    public void showTurnFeedback(String mainText, String secondaryText, boolean success) {
        feedbackMain.setText(mainText);
        feedbackSecondary.setText(secondaryText);
        VintageTheme.feedbackPanel(feedbackBox, success);
        feedbackBox.setManaged(true);
        feedbackBox.setVisible(true);
        feedbackBox.setOpacity(0);
        playFeedbackFade();
        restartHideTimer();
    }

    /**
     * Shows or hides the confrontation warning.
     *
     * @param confrontation true during confrontation
     */
    public void setConfrontation(boolean confrontation) {
        confrontationBanner.setVisible(confrontation);
        confrontationBanner.setManaged(confrontation);
        if (confrontation) {
            playConfrontationFade();
        }
    }

    private void configureFeedback() {
        feedbackBox.setMinWidth(0);
        feedbackBox.setMaxWidth(Double.MAX_VALUE);
        feedbackBox.setAlignment(Pos.CENTER);
        feedbackBox.setVisible(false);
        feedbackBox.setManaged(false);
        VintageTheme.label(feedbackMain, 14, Color.rgb(230, 216, 185), FontWeight.BOLD, false);
        VintageTheme.label(feedbackSecondary, 11, Color.rgb(169, 156, 131), FontWeight.NORMAL, true);
        feedbackMain.setWrapText(true);
        feedbackSecondary.setWrapText(true);
        feedbackMain.setMinWidth(0);
        feedbackSecondary.setMinWidth(0);
        feedbackBox.getChildren().addAll(feedbackMain, feedbackSecondary);
    }

    private void configureConfrontationBanner() {
        confrontationBanner.setVisible(false);
        confrontationBanner.setManaged(false);
        confrontationBanner.setPadding(new Insets(5, 10, 5, 10));
        confrontationBanner.setWrapText(true);
        confrontationBanner.setMinWidth(0);
        VintageTheme.label(confrontationBanner, 20, Color.rgb(207, 116, 105), FontWeight.BOLD, false);
        confrontationBanner.setBackground(VintageTheme.solid(Color.rgb(75, 26, 24, 0.45)));
        confrontationBanner.setBorder(VintageTheme.border(Color.rgb(130, 74, 68), 1));
    }

    private void playFeedbackFade() {
        FadeTransition fade = new FadeTransition(Duration.millis(180), feedbackBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void restartHideTimer() {
        if (feedbackHideTransition != null) {
            feedbackHideTransition.stop();
        }
        feedbackHideTransition = new PauseTransition(Duration.seconds(2.2));
        feedbackHideTransition.setOnFinished(event -> hideFeedback());
        feedbackHideTransition.play();
    }

    private void hideFeedback() {
        feedbackBox.setVisible(false);
        feedbackBox.setManaged(false);
    }

    private void playConfrontationFade() {
        confrontationBanner.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), confrontationBanner);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
