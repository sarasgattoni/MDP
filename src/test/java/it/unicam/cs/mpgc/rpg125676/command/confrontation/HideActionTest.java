package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HideActionTest {

    private GameSettings settings;
    private GameState state;
    private Player player;
    private Presence presence;
    private DefaultHouse house;
    private Room study;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        Room hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        Room livingRoom = new Room("living_room", "Living Room", true, RoomRole.STANDARD, false);
        house = new DefaultHouse();
        house.addRoom(study);
        house.addRoom(hallway);
        house.addRoom(livingRoom);
        house.connectRooms("study", "hallway");
        house.connectRooms("study", "living_room");
        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), study, settings.content().memoryCount());
        presence = new Presence(study, settings.presence().minAttention(), settings.presence().maxAttention());
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
    void successfulHideShouldMovePlayerAndLeavePresenceBehind() {
        presence.increaseAttention(6);
        List<Room> adjacentRooms = List.copyOf(house.getAdjacentRooms(study));
        ActionResult result = new HideAction(fixedDice(6)).execute(state);

        assertTrue(result.succeeded());
        assertTrue(adjacentRooms.contains(player.getCurrentRoom()));
        assertEquals(study, presence.getCurrentRoom());
        assertEquals(6, presence.getAttention());
        assertEquals(GamePhase.EXPLORATION, state.getPhase());
        assertTrue(player.getCurrentRoom().isVisited());
    }

    @Test
    void failedHideShouldDamagePlayerAndKeepConfrontation() {
        ActionResult result = new HideAction(fixedDice(1)).execute(state);

        assertFalse(result.succeeded());
        assertEquals(settings.player().initialLucidity() - settings.confrontation().damage(), player.getStats().getLucidity());
        assertEquals(study, player.getCurrentRoom());
        assertEquals(study, presence.getCurrentRoom());
        assertEquals(GamePhase.CONFRONTATION, state.getPhase());
        assertTrue(state.isRecoveryAvailable());
    }


}