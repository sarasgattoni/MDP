package it.unicam.cs.mpgc.rpg125676.model.game;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState state;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();

        Room entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        Room hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(hallway);
        house.connectRooms("entrance", "hallway");

        Player player = new Player("Test Player", new PlayerStats(settings.player()),
                        new PlayerInventory(), entrance, settings.content().memoryCount());
        Presence presence = new Presence(hallway, settings.presence().minAttention(), settings.presence().maxAttention());

        state = new GameState(settings, house, player, presence);
    }

    @Test
    void newGameShouldBeInProgress() {
        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
        assertFalse(state.isFinished());
    }

    @Test
    void newGameShouldStartAtTurnZero() {
        assertEquals(0, state.getTurnCount());
    }

    @Test
    void noiseTargetShouldInitiallyBeEmpty() {
        assertTrue(state.getNoiseTarget().isEmpty());
    }

    @Test
    void playerAndPresenceInDifferentRoomsShouldMeanExploration() {
        assertEquals(GamePhase.EXPLORATION, state.getPhase());
    }

    @Test
    void listenShouldBecomeAvailableAfterCooldown() {
        state.startListenCooldown();
        assertFalse(state.canListen());
        assertEquals(3, state.getListenCooldownRemaining());
        state.advanceTurn();
        state.advanceTurn();
        state.advanceTurn();
        assertTrue(state.canListen());
        assertEquals(0, state.getListenCooldownRemaining());
    }

    @Test
    void recoveryOpportunityShouldBeConsumable() {
        assertFalse(state.isRecoveryAvailable());
        state.enableRecovery();
        assertTrue(state.isRecoveryAvailable());
        state.consumeRecovery();
        assertFalse(state.isRecoveryAvailable());
    }

}