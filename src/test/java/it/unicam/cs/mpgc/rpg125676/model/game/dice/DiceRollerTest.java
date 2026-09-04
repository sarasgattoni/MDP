package it.unicam.cs.mpgc.rpg125676.model.game.dice;

import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceRollerTest {

    @Test
    void rollsShouldAlwaysStayInsideConfiguredRange() {
        GameSettings settings = GameSettings.standard();

        DiceRoller dice = new DiceRoller(settings.dice());

        for (int i = 0; i < 1000; i++) {
            int result = dice.roll();
            assertTrue(result >= 1 && result <= settings.dice().faces());
        }
    }
}