package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.movement.BfsPathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortestPathPresenceMovementStrategyTest {

    @Test
    void huntingPresenceShouldMoveOneRoomTowardTarget() {
        GameSettings settings = GameSettings.standard();

        Room entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        Room hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        Room study = new Room("study", "Study", true, RoomRole.STANDARD, false);

        DefaultHouse house = new DefaultHouse();

        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(study);
        house.connectRooms("entrance", "hallway");
        house.connectRooms("hallway", "study");
        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        Presence presence = new Presence(study, settings.presence().minAttention(), settings.presence().maxAttention());
        presence.increaseAttention(5);

        GameState state = new GameState(settings, house, player, presence);
        state.setNoiseTarget(entrance);
        PresenceMovementStrategy movement = new ShortestPathPresenceMovementStrategy(new BfsPathFinder(), new PresenceStateResolver(settings.presence()));
        movement.move(state);

        assertEquals(hallway, presence.getCurrentRoom());
    }
}