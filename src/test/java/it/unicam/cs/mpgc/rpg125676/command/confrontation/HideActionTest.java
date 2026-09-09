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

class HideActionTest {

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

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), hallway, settings.content().memoryCount());
        presence = new Presence(hallway, settings.presence().minAttention(), settings.presence().maxAttention());
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
    void successfulHideShouldMovePresenceToAdjacentRoom() {
        DiceRoller dice = fixedDice(6);
        HideAction action = new HideAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.SUCCESS, result.outcome());
        assertEquals(study, presence.getCurrentRoom());
    }

    @Test
    void successfulHideShouldDecreaseAttentionByFour() {
        presence.increaseAttention(8);
        DiceRoller dice = fixedDice(6);
        HideAction action = new HideAction(dice);
        action.execute(state);

        assertEquals(4, presence.getAttention());
    }

    @Test
    void successfulHideShouldEndConfrontation() {
        DiceRoller dice = fixedDice(6);
        HideAction action = new HideAction(dice);
        action.execute(state);

        assertEquals(GamePhase.EXPLORATION, state.getPhase());
        assertNotEquals(player.getCurrentRoom(), presence.getCurrentRoom());
    }

    @Test
    void failedHideShouldDamagePlayer() {
        DiceRoller dice = fixedDice(1);
        HideAction action = new HideAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.FAILURE, result.outcome());
        assertEquals(8, player.getStats().getLucidity());
        assertTrue(state.isRecoveryAvailable());
    }

    @Test
    void failedHideShouldNotMovePresence() {
        DiceRoller dice = fixedDice(1);
        HideAction action = new HideAction(dice);
        action.execute(state);

        assertEquals(hallway, presence.getCurrentRoom());
        assertEquals(GamePhase.CONFRONTATION, state.getPhase());
    }

    @Test
    void failedHideShouldNotDecreaseAttention() {
        presence.increaseAttention(8);
        DiceRoller dice = fixedDice(1);
        HideAction action = new HideAction(dice);
        action.execute(state);

        assertEquals(8, presence.getAttention());
    }

    @Test
    void hideShouldBeRejectedOutsideConfrontation() {
        presence.moveTo(study);
        state.synchronizePhaseWithPositions();
        DiceRoller dice = fixedDice(6);
        HideAction action = new HideAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }
    @Test
    void successfulHideShouldNotDecreaseAttentionWithActiveDecoy() {
        presence.increaseAttention(8);
        PlacedDecoy decoy = new PlacedDecoy(presence.getCurrentRoom(), settings.decoy().holdTurns());
        decoy.activate();
        state.placeDecoy(decoy);
        HideAction action = new HideAction(fixedDice(6));
        action.execute(state);

        assertEquals(8, presence.getAttention());
    }
}