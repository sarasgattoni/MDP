package it.unicam.cs.mpgc.rpg125676.command.exploration;

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

class ListenActionTest {

    private GameState state;
    private Room presenceRoom;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        Room entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        presenceRoom = new Room("attic", "Attic", true, RoomRole.STANDARD, false);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(presenceRoom);
        house.connectRooms("entrance", "attic");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        Presence presence = new Presence(presenceRoom, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void listenShouldBeSilent() {
        ListenAction action = new ListenAction();
        ActionResult result = action.execute(state);

        assertEquals(0, result.noise());
        assertTrue(result.noiseSource().isEmpty());
    }

    @Test
    void listenShouldRevealPresenceRoom() {
        ListenAction action =new ListenAction();
        ActionResult result = action.execute(state);

        assertTrue(result.message().contains(presenceRoom.getName()));
    }

    @Test
    void listenShouldHaveCooldown() {
        ListenAction action = new ListenAction();
        ActionResult first = action.execute(state);

        assertTrue(first.succeeded());

        ActionResult immediateRetry = action.execute(state);

        assertFalse(immediateRetry.consumesTurn());

        state.advanceTurn();
        state.advanceTurn();

        assertFalse(state.canListen());

        state.advanceTurn();

        assertTrue(state.canListen());
    }

}