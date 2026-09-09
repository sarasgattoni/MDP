package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.movement.BfsPathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoyAwarePresenceMovementStrategyTest {

    private GameSettings settings;
    private GameState state;
    private Room entrance;
    private Room hallway;
    private Room study;
    private Room attic;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();

        entrance = new Room("entrance", "Entrance", false, RoomRole.START, false);
        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        attic = new Room("attic", "Attic", true, RoomRole.STANDARD, true);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(study);
        house.addRoom(attic);
        house.connectRooms("entrance", "hallway");
        house.connectRooms("hallway", "study");
        house.connectRooms("study", "attic");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), hallway, settings.content().memoryCount());
        Presence presence = new Presence(attic, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void shouldUseDefaultMovementWithoutActiveDecoy() {
        PresenceMovementStrategy defaultMovement = gameState -> gameState.getPresence().moveTo(study);
        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(defaultMovement, new BfsPathFinder());
        strategy.move(state);

        assertEquals(study, state.getPresence().getCurrentRoom());
    }

    @Test
    void shouldMoveTowardDecoyAtMaximumSpeed() {
        PlacedDecoy decoy = new PlacedDecoy(entrance, settings.decoy().holdTurns());
        decoy.activate();
        state.placeDecoy(decoy);
        PresenceMovementStrategy defaultMovement = gameState -> {};
        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(defaultMovement, new BfsPathFinder());
        strategy.move(state);


        assertEquals(hallway, state.getPresence().getCurrentRoom());
    }

    @Test
    void arrivalShouldNotConsumeFirstHoldTurn() {
        state.getPresence().moveTo(study);
        PlacedDecoy decoy = new PlacedDecoy(hallway, settings.decoy().holdTurns());
        decoy.activate();
        state.placeDecoy(decoy);
        PresenceMovementStrategy defaultMovement = gameState -> {};
        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(defaultMovement, new BfsPathFinder());
        strategy.move(state);

        assertEquals(hallway, state.getPresence().getCurrentRoom());
        assertTrue(decoy.hasPresenceReached());
        assertEquals(4, decoy.getRemainingHoldTurns());
    }

    @Test
    void shouldHoldPresenceForFourCompleteTurns() {
        state.getPresence().moveTo(study);
        PlacedDecoy decoy = new PlacedDecoy(study, settings.decoy().holdTurns());
        decoy.activate();
        decoy.markPresenceReached();
        state.placeDecoy(decoy);
        PresenceMovementStrategy defaultMovement = gameState -> gameState.getPresence().moveTo(attic);
        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(defaultMovement, new BfsPathFinder());
        for (int i = 0; i < 4; i++) {
            strategy.move(state);
            assertEquals(study, state.getPresence().getCurrentRoom());
        }

        assertFalse(state.hasPlacedDecoy());

        strategy.move(state);

        assertEquals(attic, state.getPresence().getCurrentRoom());
    }

    @Test
    void shouldAttractPresenceAgainAfterItLeavesDecoyRoom() {
        state.getPresence().moveTo(hallway);
        PlacedDecoy decoy = new PlacedDecoy(hallway, settings.decoy().holdTurns());
        decoy.activate();
        decoy.markPresenceReached();
        state.placeDecoy(decoy);
        state.getPresence().moveTo(attic);
        PresenceMovementStrategy defaultMovement = gameState -> {};
        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(defaultMovement, new BfsPathFinder());
        strategy.move(state);

        assertEquals(hallway, state.getPresence().getCurrentRoom());
        assertTrue(decoy.hasPresenceReached());
        assertEquals(settings.decoy().holdTurns(), decoy.getRemainingHoldTurns());
    }
}