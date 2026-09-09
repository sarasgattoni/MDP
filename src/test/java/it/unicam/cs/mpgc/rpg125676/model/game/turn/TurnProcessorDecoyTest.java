package it.unicam.cs.mpgc.rpg125676.model.game.turn;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.StandardAttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.game.end.GameEndEvaluator;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnProcessorDecoyTest {

    private GameSettings settings;
    private GameState state;
    private Presence presence;
    private Room playerRoom;
    private Room decoyRoom;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        playerRoom = new Room("entrance", "Entrance", false, RoomRole.START, false);
        decoyRoom = new Room("study", "Study", true, RoomRole.STANDARD, true);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(playerRoom);
        house.addRoom(decoyRoom);
        house.connectRooms("entrance", "study");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), playerRoom, settings.content().memoryCount());
        presence = new Presence(decoyRoom, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void noisyActionShouldNotChangeAttentionWhileDecoyIsActive() {
        presence.increaseAttention(5);
        activateDecoy();
        createProcessor().completeTurn(ActionResult.success(3, playerRoom, "Noise"), GamePhase.EXPLORATION);

        assertEquals(5, presence.getAttention());
    }

    @Test
    void silentActionShouldNotChangeAttentionWhileDecoyIsActive() {
        presence.increaseAttention(5);
        activateDecoy();
        createProcessor().completeTurn(ActionResult.silentSuccess("Silent"), GamePhase.EXPLORATION);

        assertEquals(5, presence.getAttention());
    }

    @Test
    void normalAttentionRuleShouldResumeWithoutActiveDecoy() {
        presence.increaseAttention(5);
        createProcessor().completeTurn(ActionResult.silentSuccess("Silent"), GamePhase.EXPLORATION);

        assertEquals(4, presence.getAttention());
    }

    private void activateDecoy() {
        PlacedDecoy decoy = new PlacedDecoy(decoyRoom, settings.decoy().holdTurns());
        decoy.activate();
        state.placeDecoy(decoy);
    }

    private TurnProcessor createProcessor() {
        PresenceMovementStrategy noMovement = ignored -> {};
        return new TurnProcessor(state, new StandardAttentionPolicy(), noMovement, new GameEndEvaluator());
    }
}