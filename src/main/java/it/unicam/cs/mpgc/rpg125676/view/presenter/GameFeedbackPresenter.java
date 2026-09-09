package it.unicam.cs.mpgc.rpg125676.view.presenter;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.command.confrontation.FacePresenceAction;
import it.unicam.cs.mpgc.rpg125676.command.confrontation.HideAction;
import it.unicam.cs.mpgc.rpg125676.command.exploration.SearchAction;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.view.component.PlayerPanel;
import it.unicam.cs.mpgc.rpg125676.view.component.RoomPanel;

/**
 * Converts game mechanics into user-facing feedback for the game view.
 * It derives action, noise and probability information from the current
 * game state and updates the related visual components.
 */
public class GameFeedbackPresenter {

    private final PlayerPanel playerPanel;
    private final RoomPanel roomPanel;

    public GameFeedbackPresenter(PlayerPanel playerPanel, RoomPanel roomPanel) {
        this.playerPanel = playerPanel;
        this.roomPanel = roomPanel;
    }

    public void refresh(GameState state) {
        updateAttributeTooltips(state);
        roomPanel.setConfrontation(state.isPlayerWithPresence());
    }

    /**
     * Creates the primary feedback message for the completed action.
     * Dice-based actions include the roll, relevant attribute and value;
     * other actions receive a generic success or failure message.
     *
     * @param action executed action
     * @param result result produced by the action
     * @param state current game state
     * @param diceRoller dice roller used by dice-based actions
     * @return main feedback message
     */
    public String createMainFeedback(GameAction action, ActionResult result, GameState state, DiceRoller diceRoller) {
        if (action instanceof SearchAction) {
            return diceMessage(diceRoller.getLastRoll(), "Caution", state.getPlayer().getStats().getCaution(), state.getSettings().search().silentSearchThreshold());
        }
        if (action instanceof FacePresenceAction) {
            return diceMessage(diceRoller.getLastRoll(), "Composure", state.getPlayer().getStats().getComposure(), state.getSettings().confrontation().facePresenceThreshold());
        }
        if (action instanceof HideAction) {
            return diceMessage(diceRoller.getLastRoll(), "Caution", state.getPlayer().getStats().getCaution(), state.getSettings().confrontation().hideThreshold());
        }
        return result.succeeded() ? "ACTION COMPLETED" : "ACTION FAILED";
    }

    /**
     * Creates the feedback message describing the noise generated during a turn.
     * While a decoy is active, the message reports that Attention is frozen
     * instead of displaying the normal noise-based Attention feedback.
     *
     * @param result result of the player's action
     * @param state current game state
     * @param decoyActive whether the decoy produces noise during the turn
     * @param decoyRoom room containing the ringing decoy, if present
     * @param startingPhase phase in which the turn started
     * @return noise feedback message
     */
    public String createNoiseFeedback(ActionResult result, GameState state, boolean decoyActive, Room decoyRoom, GamePhase startingPhase) {
        if (decoyActive) {
            String location = decoyRoom == null ? "" : " in " + decoyRoom.getName();
            return "DECOY RINGING" + location + " — Attention frozen.";
        }
        if (startingPhase == GamePhase.CONFRONTATION) {
            if (decoyActive && decoyRoom != null) {
                return "Alarm clock rings in " + decoyRoom.getName() + " — Attention rule suspended during confrontation.";
            }
            return "CONFRONTATION — Attention rule suspended.";
        }
        int totalNoise = result.noise();
        if (decoyActive) {
            totalNoise += state.getSettings().noise().decoyNoise();
        }
        if (totalNoise == 0) {
            return "SILENT — Attention decreases.";
        }
        String message = "NOISE +" + totalNoise;
        if (decoyActive && decoyRoom != null) {
            message += " — Alarm clock rings in " + decoyRoom.getName();
        }
        return message;
    }

    public void showTurnFeedback(String main, String secondary, boolean success) {
        roomPanel.showTurnFeedback(main, secondary, success);
    }

    private void updateAttributeTooltips(GameState state) {
        int composure = state.getPlayer().getStats().getComposure();
        int caution = state.getPlayer().getStats().getCaution();
        int faces = state.getSettings().dice().faces();
        int faceChance = successChance(composure, state.getSettings().confrontation().facePresenceThreshold(), faces);
        int hideChance = successChance(caution, state.getSettings().confrontation().hideThreshold(), faces);
        int searchChance = successChance(caution, state.getSettings().search().silentSearchThreshold(), faces);
        playerPanel.setComposureTooltip("Face Presence success: " + faceChance + "%");
        playerPanel.setCautionTooltip("Hide success: " + hideChance + "%\nSilent Search: " + searchChance + "%");
    }

    private int successChance(int attribute, int threshold, int faces) {
        int successfulRolls = 0;
        for (int roll = 1; roll <= faces; roll++) {
            if (roll + attribute >= threshold) {
                successfulRolls++;
            }
        }
        return (int) Math.round(successfulRolls * 100.0 / faces);
    }

    private String diceMessage(int roll, String attributeName, int attributeValue, int threshold) {
        return "D6 " + roll + " + " + attributeName + " " + attributeValue + " = " + (roll + attributeValue) + " / " + threshold;
    }
}
