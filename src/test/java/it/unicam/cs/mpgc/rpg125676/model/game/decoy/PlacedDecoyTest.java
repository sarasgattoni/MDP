package it.unicam.cs.mpgc.rpg125676.model.game.decoy;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlacedDecoyTest {

    private final Room room = new Room("study", "Study", true, RoomRole.STANDARD, false);

    @Test
    void shouldStartInactive() {
        PlacedDecoy decoy = new PlacedDecoy(room, 4);

        assertFalse(decoy.isRinging());
        assertFalse(decoy.hasPresenceReached());
        assertFalse(decoy.isExpired());
        assertEquals(4, decoy.getRemainingHoldTurns());
    }

    @Test
    void shouldNotConsumeTurnsBeforePresenceReachesIt() {
        PlacedDecoy decoy = new PlacedDecoy(room, 4);
        decoy.activate();
        decoy.consumeHoldTurn();

        assertEquals(4, decoy.getRemainingHoldTurns());
    }

    @Test
    void shouldExpireAfterFourHoldTurns() {
        PlacedDecoy decoy = new PlacedDecoy(room, 4);
        decoy.activate();
        decoy.markPresenceReached();

        for (int i = 0; i < 3; i++) {
            decoy.consumeHoldTurn();
        }

        assertFalse(decoy.isExpired());
        assertEquals(1, decoy.getRemainingHoldTurns());

        decoy.consumeHoldTurn();

        assertTrue(decoy.isExpired());
    }

    @Test
    void shouldRejectSecondActivation() {
        PlacedDecoy decoy = new PlacedDecoy(room, 4);
        decoy.activate();

        assertThrows(IllegalStateException.class, decoy::activate);
    }
}