package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.presence.PresenceState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PresenceStateResolverTest {

    private PresenceStateResolver resolver;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        resolver = new PresenceStateResolver(settings.presence());
    }

    @Test
    void attentionFromZeroToFourShouldBeDormant() {
        assertEquals(PresenceState.DORMANT, resolver.resolve(0));
        assertEquals(PresenceState.DORMANT, resolver.resolve(4));
    }

    @Test
    void attentionFromFiveToEightShouldBeHunting() {
        assertEquals(PresenceState.HUNTING, resolver.resolve(5));
        assertEquals(PresenceState.HUNTING, resolver.resolve(8));
    }

    @Test
    void attentionFromNineToTenShouldBeUnleashed() {
        assertEquals(PresenceState.UNLEASHED, resolver.resolve(9));
        assertEquals(PresenceState.UNLEASHED, resolver.resolve(10));
    }

    @Test
    void dormantPresenceShouldNotMove() {
        assertEquals(0, resolver.movementSteps(PresenceState.DORMANT));
    }

    @Test
    void huntingPresenceShouldMoveOneRoom() {
        assertEquals(1, resolver.movementSteps(PresenceState.HUNTING));
    }

    @Test
    void unleashedPresenceShouldMoveTwoRooms() {
        assertEquals(2, resolver.movementSteps(PresenceState.UNLEASHED));
    }
}