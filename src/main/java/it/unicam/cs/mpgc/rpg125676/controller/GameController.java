package it.unicam.cs.mpgc.rpg125676.controller;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.command.confrontation.FacePresenceAction;
import it.unicam.cs.mpgc.rpg125676.command.confrontation.HideAction;
import it.unicam.cs.mpgc.rpg125676.command.exploration.*;
import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.view.GameView;
import it.unicam.cs.mpgc.rpg125676.view.SceneNavigator;
import it.unicam.cs.mpgc.rpg125676.view.presenter.GameFeedbackPresenter;
import it.unicam.cs.mpgc.rpg125676.view.support.AtmosphericEventProvider;
import it.unicam.cs.mpgc.rpg125676.view.support.GameDialogManager;
import it.unicam.cs.mpgc.rpg125676.view.support.GameNarrativeCatalog;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Coordinates an active game session with the JavaFX game view.
 * The controller translates user interactions into game actions,
 * delegates their execution to the {@link GameEngine} and refreshes
 * the view according to the resulting game state.
 * It also coordinates turn feedback, pending player decisions,
 * save operations, temporary Presence reveals and navigation when
 * the game ends.
 */
public class GameController {

    private final GameView view;
    private final GameSession session;
    private final SceneNavigator navigator;
    private final GameNarrativeCatalog narrativeCatalog;
    private final GameDialogManager dialogs;
    private final AtmosphericEventProvider atmosphericEvents;

    private GameEngine engine;
    private DiceRoller diceRoller;
    private GameFeedbackPresenter feedbackPresenter;
    private PauseTransition presenceHideTransition;

    /**
     * Creates a controller for the supplied game view and session.
     *
     * @param view game view controlled by this instance
     * @param session current application game session
     * @param navigator navigator used to change application screens
     * @throws NullPointerException if any dependency is null
     */
    public GameController(GameView view, GameSession session, SceneNavigator navigator) {
        this.view = Objects.requireNonNull(view);
        this.session = Objects.requireNonNull(session);
        this.navigator = Objects.requireNonNull(navigator);
        this.narrativeCatalog = new GameNarrativeCatalog();
        this.dialogs = new GameDialogManager(narrativeCatalog);
        this.atmosphericEvents = new AtmosphericEventProvider();
    }

    /**
     * Initializes the controller for the current game.
     * The method obtains the active game engine, creates the objects
     * required by the view and connects all user actions before
     * performing the first refresh.
     */
    public void initialize() {
        engine = session.getCurrentGame();
        diceRoller = new DiceRoller(engine.getState().getSettings().dice());
        feedbackPresenter = new GameFeedbackPresenter(view.getPlayerPanel(), view.getRoomPanel());

        wireActions();
        view.initializeHouse(engine.getState());
        view.getRoomPanel().initializeLog(engine.getState().getTurnCount() == 0);
        refresh();
    }

    /**
     * Connects the controls exposed by the game view to their
     * corresponding controller operations and game actions.
     */
    private void wireActions() {
        var actions = view.getActionPanel();
        actions.onMove(this::move);
        actions.onSearch(() -> executeAction(new SearchAction(diceRoller)));
        actions.onListen(() -> executeAction(new ListenAction()));
        actions.onCatchBreath(() -> executeAction(new CatchBreathAction()));
        actions.onPlaceDecoy(() -> executeAction(new PlaceDecoyAction()));
        actions.onActivateDecoy(() -> executeAction(new ActivateDecoyAction()));
        actions.onSpeakName(() -> executeAction(new SpeakNameAction()));
        actions.onFacePresence(() -> executeAction(new FacePresenceAction(diceRoller)));
        actions.onHide(() -> executeAction(new HideAction(diceRoller)));
        view.onSave(this::save);
        view.onSaveAndMenu(this::saveAndReturnToMenu);
    }

    /**
     * Executes a movement action toward the destination currently
     * selected by the player.
     * If no destination is selected, the action is not executed and
     * a warning is shown instead.
     */
    private void move() {
        Room destination = view.getActionPanel().getSelectedDestination();
        if (destination == null) {
            dialogs.showWarning("Choose a destination first.");
            return;
        }
        executeAction(new MoveAction(destination));
    }

    /**
     * Executes the supplied game action and coordinates all resulting
     * updates required by the user interface.
     * The method records information about the state before execution,
     * delegates the action to the game engine, resolves pending decisions,
     * refreshes the view and handles feedback related to noise, Presence
     * movement and game completion.
     *
     * @param action action requested by the player
     */
    private void executeAction(GameAction action) {
        GameState stateBefore = engine.getState();
        GamePhase startingPhase = stateBefore.getPhase();
        Room presenceBeforeTurn = stateBefore.getPresence().getCurrentRoom();

        boolean decoyWillRing = stateBefore.getPlacedDecoy()
                .map(decoy -> decoy.isRinging())
                .orElse(false)
                || action instanceof ActivateDecoyAction;

        Room decoyRoom = stateBefore.getPlacedDecoy()
                .map(decoy -> decoy.getRoom())
                .orElse(null);
        ActionResult result;
        try {
            result = engine.execute(action);
        } catch (RuntimeException exception) {
            dialogs.showError("Action failed", exception.getMessage());
            return;
        }

        view.getRoomPanel().appendEvent(result.message());

        String mainFeedback = null;
        String noiseFeedback = null;
        if (result.consumesTurn()) {
            mainFeedback = feedbackPresenter.createMainFeedback(action, result, engine.getState(), diceRoller);
            noiseFeedback = feedbackPresenter.createNoiseFeedback(result, engine.getState(), decoyWillRing, decoyRoom, startingPhase);
        }

        resolvePendingDecision();
        refresh();

        if (result.consumesTurn()) {
            feedbackPresenter.showTurnFeedback(mainFeedback, noiseFeedback, result.succeeded());
        }

        if (action instanceof ListenAction && result.succeeded() && !engine.getState().isPlayerWithPresence()) {
            revealPresenceTemporarily(presenceBeforeTurn);
        }
        appendPresenceMovementFeedback(action, presenceBeforeTurn);

        if (action instanceof MoveAction && result.succeeded() && !engine.getState().isFinished()) {
            atmosphericEvents.nextEvent().ifPresent(view.getRoomPanel()::appendEvent);
        }
        handleGameEnd();
    }

    /**
     * Resolves any player decision currently requested by the game state.
     * Memory decisions are presented through the dialog manager and
     * delegated to the game engine before the suspended turn continues.
     */
    private void resolvePendingDecision() {
        while (engine.getState().getPendingDecision().isPresent()) {
            int memoryNumber = engine.getState().getPlayer().getMemoriesFound();
            PlayerAttribute choice = dialogs.requestMemoryAttribute(memoryNumber);
            view.getRoomPanel().appendEvent(engine.resolveMemoryDecision(choice));
        }
    }

    /**
     * Adds narrative feedback when the Presence changes room during
     * the completed action.
     *
     * @param action action that caused the completed turn
     * @param previousRoom Presence room before the turn was resolved
     */
    private void appendPresenceMovementFeedback(GameAction action, Room previousRoom) {
        GameState state = engine.getState();
        Room currentPresenceRoom = state.getPresence().getCurrentRoom();

        if (previousRoom.equals(currentPresenceRoom)) {
            return;
        }

        if (state.isPlayerWithPresence()) {
            view.getRoomPanel().appendEvent("Footsteps rush closer. The Presence enters " + currentPresenceRoom.getName() + ".");
            return;
        }

        if (action instanceof ListenAction) {
            view.getRoomPanel().appendEvent("After listening, you hear footsteps moving somewhere through the house.");
        }
    }

    /**
     * Temporarily reveals the Presence on the house map.
     * Any previous reveal timer is cancelled before a new temporary
     * reveal is started.
     *
     * @param room room in which the Presence must be shown
     */
    private void revealPresenceTemporarily(Room room) {
        var map = view.getPlayerPanel().getHouseMapView();
        map.showPresence(room);

        if (presenceHideTransition != null) {
            presenceHideTransition.stop();
        }

        presenceHideTransition = new PauseTransition(Duration.seconds(2.5));
        presenceHideTransition.setOnFinished(event -> map.hidePresence());
        presenceHideTransition.play();
    }

    /**
     * Refreshes the game view and turn feedback from the current
     * game state.
     */
    private void refresh() {
        GameState state = engine.getState();
        view.refresh(state, narrativeCatalog);
        feedbackPresenter.refresh(state);
    }

    /**
     * Saves the current unfinished game and reports the result
     * to the player.
     */
    private void save() {
        try {
            session.saveCurrentGame();
            dialogs.showInformation("Game saved", "Your progress has been saved.");
        } catch (RuntimeException exception) {
            dialogs.showError("Save failed", exception.getMessage());
        }
    }

    /**
     * Saves the current game when necessary and returns to the
     * main menu.
     */
    private void saveAndReturnToMenu() {
        try {
            if (!engine.getState().isFinished()) {
                session.saveCurrentGame();
            }
            navigator.showMainMenu();
        } catch (RuntimeException exception) {
            dialogs.showError("Unable to return to menu", exception.getMessage());
        }
    }

    /**
     * Completes application-level processing after the game finishes.
     * The final result is recorded through the game session and the
     * application is then redirected to the game-over screen.
     */
    private void handleGameEnd() {
        GameState state = engine.getState();
        if (!state.isFinished()) {
            return;
        }

        try {
            session.completeCurrentGame();
        } catch (RuntimeException exception) {
            dialogs.showError("Unable to record game result", exception.getMessage());
        }

        navigator.showGameOver();
    }
}
