package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatchBreathActionTest {

    private Player player;
    private Presence presence;
    private GameState state;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        Room entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        Room hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        DefaultHouse house =new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(hallway);
        house.connectRooms("entrance", "hallway");

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        presence = new Presence(hallway, settings.presence().minAttention(),settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void shouldRecoverLucidityWhenAttentionIsZero() {
        player.getStats().loseLucidity(2);
        state.enableRecovery();
        CatchBreathAction action = new CatchBreathAction();
        action.execute(state);

        assertEquals(9, player.getStats().getLucidity());
    }

    @Test
    void shouldBeRejectedWhenPresenceHasAttention() {
        player.getStats().loseLucidity(2);
        presence.increaseAttention(1);
        CatchBreathAction action = new CatchBreathAction();
        ActionResult result = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertEquals(8, player.getStats().getLucidity());
        assertFalse(result.consumesTurn());
    }

    @Test
    void shouldConsumeRecoveryOpportunity() {
        player.getStats().loseLucidity(2);
        state.enableRecovery();
        CatchBreathAction action = new CatchBreathAction();
        action.execute(state);

        assertFalse(state.isRecoveryAvailable());

        ActionResult secondAttempt = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, secondAttempt.outcome());
    }

}