package it.unicam.cs.mpgc.rpg125676.model.game.turn;

import it.unicam.cs.mpgc.rpg125676.command.exploration.ActivateDecoyAction;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.StandardAttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.game.end.GameEndEvaluator;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.DefaultGameEngine;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnProcessorDecoyTest {

    @Test
    void activationTurnShouldGenerateNoiseFromDecoyRoom() {
        GameSettings settings = GameSettings.standard();
        Room entrance = new Room("entrance", "Entrance", false, RoomRole.START, false);
        Room study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        Room attic = new Room("attic", "Attic", true, RoomRole.STANDARD, true);

        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(study);
        house.addRoom(attic);
        house.connectRooms("entrance", "study");
        house.connectRooms("study", "attic");

        Player player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());

        Presence presence = new Presence(attic, settings.presence().minAttention(), settings.presence().maxAttention());
        GameState state = new GameState(settings, house, player, presence);
        state.placeDecoy(new PlacedDecoy(study, settings.decoy().holdTurns()));
        PresenceMovementStrategy noMovement = new PresenceMovementStrategy() {
            @Override
            public void move(GameState state) {}
        };
        TurnProcessor processor = new TurnProcessor(state, new StandardAttentionPolicy(), noMovement, new GameEndEvaluator());
        DefaultGameEngine engine = new DefaultGameEngine(state, processor);
        engine.execute(new ActivateDecoyAction());

        assertEquals(settings.noise().decoyNoise(), presence.getAttention());
        assertEquals(study, state.getNoiseTarget().orElseThrow());
        assertEquals(1, state.getTurnCount());
    }
}
