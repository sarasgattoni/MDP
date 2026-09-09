package it.unicam.cs.mpgc.rpg125676.model.game.decoy;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlacedDecoyTest {

    private PlacedDecoy decoy;

    @BeforeEach
    void setUp() {
        Room room = new Room("study", "Study", true, RoomRole.STANDARD, false);
        decoy = new PlacedDecoy(room, 4);
    }

    @Test
    void shouldStartInactive() {
        assertFalse(decoy.isRinging());
        assertFalse(decoy.hasPresenceReached());
        assertEquals(4, decoy.getRemainingHoldTurns());
    }

    @Test
    void shouldExpireAfterFourHoldTurns() {
        decoy.activate();
        decoy.markPresenceReached();
        for (int i = 0; i < 4; i++) {
            decoy.consumeHoldTurn();
        }

        assertTrue(decoy.isExpired());
    }

    @Test
    void releaseShouldPreserveRemainingTurns() {
        decoy.activate();
        decoy.markPresenceReached();
        decoy.consumeHoldTurn();
        decoy.releasePresence();

        assertFalse(decoy.hasPresenceReached());
        assertEquals(3, decoy.getRemainingHoldTurns());
    }
}