package it.unicam.cs.mpgc.rpg125676.model.game.decoy;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActiveDecoyTest {

    @Test
    void placementTurnShouldNotProduceNoise() {
        ActiveDecoy decoy = new ActiveDecoy(new Room("attic", "Attic", true, RoomRole.STANDARD, true), 4);

        assertFalse(decoy.consumeNoiseTurn());

        assertEquals(4, decoy.getRemainingNoiseTurns());
    }

    @Test
    void shouldProduceNoiseForFourFollowingTurns() {
        ActiveDecoy decoy = new ActiveDecoy(new Room("attic", "Attic", true, RoomRole.STANDARD, true), 4);
        decoy.consumeNoiseTurn();
        assertTrue(decoy.consumeNoiseTurn());
        assertTrue(decoy.consumeNoiseTurn());
        assertTrue(decoy.consumeNoiseTurn());
        assertTrue(decoy.consumeNoiseTurn());

        assertTrue(decoy.isExpired());
        assertFalse(decoy.consumeNoiseTurn());
    }
}