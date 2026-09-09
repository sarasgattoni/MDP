package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivateDecoyActionTest {

    private GameState state;
    private Room study;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        Room attic = new Room("attic", "Attic", true, RoomRole.STANDARD, true);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(study);
        house.addRoom(attic);
        house.connectRooms("study", "attic");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), study, settings.content().memoryCount());
        Presence presence = new Presence(attic, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void shouldActivatePlacedDecoy() {
        PlacedDecoy decoy = new PlacedDecoy(study, 4);
        state.placeDecoy(decoy);
        ActionResult result = new ActivateDecoyAction().execute(state);

        assertTrue(result.succeeded());
        assertTrue(result.consumesTurn());
        assertEquals(0, result.noise());
        assertTrue(decoy.isRinging());
    }

    @Test
    void shouldRejectWhenNoDecoyIsPlaced() {
        ActionResult result = new ActivateDecoyAction().execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }

    @Test
    void shouldRejectAlreadyRingingDecoy() {
        PlacedDecoy decoy = new PlacedDecoy(study, 4);
        decoy.activate();
        state.placeDecoy(decoy);

        ActionResult result = new ActivateDecoyAction().execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }
}