package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FacePresenceActionTest {

    private GameSettings settings;

    private Room hallway;
    private Room study;

    private Player player;
    private Presence presence;
    private GameState state;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);

        DefaultHouse house = new DefaultHouse();
        house.addRoom(hallway);
        house.addRoom(study);
        house.connectRooms("hallway", "study");

        presence = new Presence(study, settings.presence().minAttention(), settings.presence().maxAttention());
        presence.moveTo(hallway);
        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), hallway, settings.content().memoryCount());
        state = new GameState(settings, house, player, presence);
    }
    private DiceRoller fixedDice(int value) {
        return new DiceRoller(settings.dice()) {
            @Override
            public int roll() {
                return value;
            }
        };
    }
    @Test
    void gameShouldStartInConfrontationPhase() {
        assertEquals(GamePhase.CONFRONTATION, state.getPhase());
    }

    @Test
    void successfulFaceShouldRetreatPresence() {
        DiceRoller dice = fixedDice(6);

        FacePresenceAction action = new FacePresenceAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.SUCCESS, result.outcome());
        assertTrue(result.succeeded());
        assertEquals(study, presence.getCurrentRoom());
    }

    @Test
    void successfulFaceShouldResetAttention() {
        presence.increaseAttention(8);
        DiceRoller dice = fixedDice(6);
        FacePresenceAction action = new FacePresenceAction(dice);
        action.execute(state);

        assertEquals(0, presence.getAttention());
    }

    @Test
    void successfulFaceShouldEndConfrontation() {
        DiceRoller dice = fixedDice(6);
        FacePresenceAction action = new FacePresenceAction(dice);
        action.execute(state);

        assertEquals(GamePhase.EXPLORATION, state.getPhase());
        assertNotEquals(player.getCurrentRoom(), presence.getCurrentRoom());
    }

    @Test
    void failedFaceShouldDamagePlayer() {
        DiceRoller dice = fixedDice(1);
        FacePresenceAction action = new FacePresenceAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.FAILURE, result.outcome());
        assertEquals(8, player.getStats().getLucidity());
        assertTrue(state.isRecoveryAvailable());
    }

    @Test
    void failedFaceShouldNotMovePresence() {
        DiceRoller dice = fixedDice(1);
        FacePresenceAction action = new FacePresenceAction(dice);
        action.execute(state);

        assertEquals(hallway, presence.getCurrentRoom());
        assertEquals(GamePhase.CONFRONTATION, state.getPhase());
    }

    @Test
    void faceShouldBeRejectedOutsideConfrontation() {
        presence.moveTo(study);
        state.synchronizePhaseWithPositions();
        DiceRoller dice = fixedDice(6);
        FacePresenceAction action = new FacePresenceAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }
    @Test
    void successfulFaceShouldEscapeConfrontationWhenPresenceHasNoMovementHistory() {
        Room stairs = new Room("stairs", "Stairs", false, RoomRole.STANDARD, false);

        Room childBedroom = new Room("child_bedroom", "Child's Bedroom", true, RoomRole.STANDARD, true);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(stairs);
        house.addRoom(childBedroom);
        house.connectRooms("stairs", "child_bedroom");

        Player localPlayer = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), childBedroom, settings.content().memoryCount());

        Presence localPresence = new Presence(childBedroom, settings.presence().minAttention(), settings.presence().maxAttention());

        GameState localState = new GameState(settings,house, localPlayer, localPresence);

        ActionResult result = new FacePresenceAction(fixedDice(6)).execute(localState);

        assertEquals(ActionOutcome.SUCCESS, result.outcome());
        assertEquals(stairs, localPresence.getCurrentRoom());
        assertEquals(GamePhase.EXPLORATION, localState.getPhase());
        assertFalse(localState.isPlayerWithPresence());
    }

    @Test
    void successfulFaceShouldNotResetAttentionWithActiveDecoy() {
        presence.increaseAttention(6);
        PlacedDecoy decoy = new PlacedDecoy(presence.getCurrentRoom(), settings.decoy().holdTurns());
        decoy.activate();
        state.placeDecoy(decoy);
        FacePresenceAction action = new FacePresenceAction(fixedDice(6));
        action.execute(state);

        assertEquals(6, presence.getAttention());
    }
}