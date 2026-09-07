package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceDecoyActionTest {
    private GameSettings settings;
    private Room searchableRoom;
    private Room presenceRoom;
    private Player player;
    private GameState state;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        searchableRoom = new Room("study", "Study", true, RoomRole.STANDARD, false);
        presenceRoom =
                new Room("attic", "Attic", true, RoomRole.STANDARD, false);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(searchableRoom);
        house.addRoom(presenceRoom);
        house.connectRooms("study", "attic");

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), searchableRoom, settings.content().memoryCount());
        Presence presence = new Presence(presenceRoom, settings.presence().minAttention(), settings.presence().maxAttention());
        state= new GameState(settings, house, player, presence);
    }
    @Test
    void shouldConsumeCollectedDecoy() {
        player.collectDecoy();
        PlaceDecoyAction action = new PlaceDecoyAction();
        ActionResult result = action.execute(state);

        assertTrue(result.succeeded());
        assertEquals(0, player.getDecoyCount());
        assertTrue(state.hasActiveDecoy());
    }

    @Test
    void shouldRejectPlacementWithoutDecoy() {
        PlaceDecoyAction action = new PlaceDecoyAction();
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }
}