package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {

    private GameSettings settings;
    private DefaultHouse house;

    private Room entrance;
    private Room hallway;
    private Room study;
    private Room lockedRoom;

    private Player player;
    private Presence presence;
    private GameState state;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        lockedRoom = new Room("locked_room", "Locked Room", false, RoomRole.FINAL, false);

        house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(study);
        house.addRoom(lockedRoom);

        house.connectRooms("entrance","hallway");
        house.connectRooms("hallway", "study");
        house.connectRooms("hallway", "locked_room");

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        presence = new Presence(study, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void shouldMoveToAdjacentRoom() {
        MoveAction action = new MoveAction(hallway);
        ActionResult result = action.execute(state);

        assertEquals(hallway, player.getCurrentRoom());
        assertEquals(ActionOutcome.SUCCESS, result.outcome());
        assertEquals(settings.noise().movementNoise(), result.noise());
        assertTrue(result.consumesTurn());
    }

    @Test
    void shouldRejectNonAdjacentRoom() {
        MoveAction action = new MoveAction(study);
        ActionResult result = action.execute(state);

        assertEquals(entrance, player.getCurrentRoom());
        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }

    @Test
    void shouldRejectLockedRoomWithoutMemories() {
        player.moveTo(hallway);
        MoveAction action = new MoveAction(lockedRoom);
        ActionResult result = action.execute(state);

        assertEquals(hallway, player.getCurrentRoom());
        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn());
    }

    @Test
    void enteringPresenceRoomShouldStartConfrontation() {
        player.moveTo(hallway);
        MoveAction action = new MoveAction(study);
        action.execute(state);
        assertEquals(study, player.getCurrentRoom());
        assertEquals(GamePhase.CONFRONTATION, state.getPhase());
    }
}