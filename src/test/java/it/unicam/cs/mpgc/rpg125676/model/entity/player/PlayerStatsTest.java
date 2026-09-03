package it.unicam.cs.mpgc.rpg125676.model.entity.player;

import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsTest {

    private PlayerStats stats;
    private GameSettings settings;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        stats = new PlayerStats(settings.player());
    }

    @Test
    void shouldStartWithConfiguredValues() {
        assertEquals(10, stats.getLucidity());
        assertEquals(4, stats.getComposure());
        assertEquals(3, stats.getCaution());
    }

    @Test
    void shouldLoseLucidity() {
        stats.loseLucidity(2);

        assertEquals(8, stats.getLucidity());
    }

    @Test
    void lucidityShouldNeverBecomeNegative() {
        stats.loseLucidity(100);

        assertEquals(0, stats.getLucidity());
        assertFalse(stats.isAlive());
    }

    @Test
    void lucidityShouldNeverExceedMaximum() {
        stats.loseLucidity(2);
        stats.recoverLucidity(100);

        assertEquals(settings.player().maxLucidity(), stats.getLucidity());
    }

    @Test
    void composureShouldNeverExceedMaximum() {
        stats.increaseComposure(100);

        assertEquals(settings.player().maxAttribute(), stats.getComposure());
    }

    @Test
    void cautionShouldNeverExceedMaximum() {
        stats.increaseCaution(100);

        assertEquals(settings.player().maxAttribute(), stats.getCaution());
    }
    @Test
    void shouldIncreaseSelectedComposureAttribute() {
        stats.increaseAttribute(PlayerAttribute.COMPOSURE, 1);

        assertEquals(5, stats.getComposure());
    }

    @Test
    void shouldIncreaseSelectedCautionAttribute() {
        stats.increaseAttribute(PlayerAttribute.CAUTION, 1);

        assertEquals(4, stats.getCaution());
    }
}