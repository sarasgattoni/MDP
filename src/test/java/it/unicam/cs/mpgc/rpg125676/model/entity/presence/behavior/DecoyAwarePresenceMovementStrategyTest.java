package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

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

class DecoyAwarePresenceMovementStrategyTest {

    private GameState state;
    private Room study;
    private Room attic;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        Room entrance = new Room("entrance", "Entrance", false, RoomRole.START, false);
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        attic = new Room("attic", "Attic", true, RoomRole.STANDARD, true);

        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(study);
        house.addRoom(attic);
        house.connectRooms("entrance", "study");
        house.connectRooms("study", "attic");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        Presence presence = new Presence(attic, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void shouldMarkArrivalWithoutConsumingHoldTurn() {
        PlacedDecoy decoy = new PlacedDecoy(study, 4);
        decoy.activate();
        state.placeDecoy(decoy);

        PresenceMovementStrategy delegate = new PresenceMovementStrategy() {
            @Override
            public void move(GameState gameState) {
                gameState.getPresence().moveTo(study);
            }
        };

        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(delegate);
        strategy.move(state);

        assertEquals(study, state.getPresence().getCurrentRoom());
        assertTrue(decoy.hasPresenceReached());
        assertEquals(4, decoy.getRemainingHoldTurns());
    }

    @Test
    void shouldHoldPresenceForFourCompleteTurns() {
        state.getPresence().moveTo(study);
        PlacedDecoy decoy = new PlacedDecoy(study, 4);
        decoy.activate();
        decoy.markPresenceReached();
        state.placeDecoy(decoy);
        PresenceMovementStrategy delegate = new PresenceMovementStrategy() {
            @Override
            public void move(GameState gameState) {
                gameState.getPresence().moveTo(attic);
            }
        };

        DecoyAwarePresenceMovementStrategy strategy = new DecoyAwarePresenceMovementStrategy(delegate);

        for (int i = 0; i < 4; i++) {
            strategy.move(state);
            assertEquals(study, state.getPresence().getCurrentRoom());
        }

        assertFalse(state.hasPlacedDecoy());

        strategy.move(state);

        assertEquals(attic, state.getPresence().getCurrentRoom());
    }
}