package it.unicam.cs.mpgc.rpg125676.model.game.attention;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnNoise;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardAttentionPolicyTest {

    private GameState state;
    private Presence presence;
    private Room playerRoom;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();

        playerRoom = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        Room presenceRoom = new Room("study", "Study", true, RoomRole.STANDARD, false);

        DefaultHouse house = new DefaultHouse();
        house.addRoom(playerRoom);
        house.addRoom(presenceRoom);
        house.connectRooms("entrance", "study");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), playerRoom, settings.content().memoryCount());

        presence = new Presence(presenceRoom, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
    }

    @Test
    void noiseShouldIncreaseAttention() {
        AttentionPolicy policy = new StandardAttentionPolicy();
        policy.apply(state, TurnNoise.of(3, playerRoom));

        assertEquals(3, presence.getAttention());
        assertEquals(playerRoom, state.getNoiseTarget().orElseThrow());
    }

    @Test
    void silenceShouldDecreaseAttention() {
        presence.increaseAttention(3);
        AttentionPolicy policy = new StandardAttentionPolicy();
        policy.apply(state, TurnNoise.silent());

        assertEquals(2, presence.getAttention());
    }
}