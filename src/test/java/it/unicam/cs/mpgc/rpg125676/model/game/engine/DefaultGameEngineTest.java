package it.unicam.cs.mpgc.rpg125676.model.game.engine;

import it.unicam.cs.mpgc.rpg125676.command.exploration.MoveAction;
import it.unicam.cs.mpgc.rpg125676.command.exploration.SearchAction;
import it.unicam.cs.mpgc.rpg125676.model.content.Memory;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceStateResolver;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.ShortestPathPresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.StandardAttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.game.end.GameEndEvaluator;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnProcessor;
import it.unicam.cs.mpgc.rpg125676.model.movement.BfsPathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGameEngineTest {

    private GameSettings settings;

    private Room entrance;
    private Room hallway;
    private Room study;

    private Player player;
    private Presence presence;

    private GameState state;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);
        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);
        study = new Room("study", "Study", true, RoomRole.STANDARD, false);
        DefaultHouse house = new DefaultHouse();

        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(study);
        house.connectRooms("entrance", "hallway");
        house.connectRooms("hallway", "study");

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        presence = new Presence(study, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);

        PresenceMovementStrategy movement = new ShortestPathPresenceMovementStrategy(new BfsPathFinder(), new PresenceStateResolver(settings.presence()));
        TurnProcessor turnProcessor = new TurnProcessor(state, new StandardAttentionPolicy(), movement, new GameEndEvaluator());
        engine = new DefaultGameEngine(state, turnProcessor);
    }
    private DiceRoller fixedDice(int value) {
        return new DiceRoller(settings.dice()) {

            @Override
            public int roll() {
                return value;
            }
        };
    }
    @Test
    void movementShouldCompleteOneTurn() {
        engine.execute(new MoveAction(hallway));

        assertEquals(1, state.getTurnCount());

        assertEquals(hallway, player.getCurrentRoom());
    }

    @Test
    void movementNoiseShouldIncreaseAttention() {
        engine.execute(new MoveAction(hallway));

        assertEquals(settings.noise().movementNoise(), presence.getAttention());
        assertEquals(hallway, state.getNoiseTarget().orElseThrow());
    }

    @Test
    void memoryChoiceShouldNotCreateSecondTurn() {
        player.moveTo(hallway);
        hallway.setContent(new Memory(1, "A memory."));
        DiceRoller successfulDice = fixedDice(6);
        engine.execute(new SearchAction(successfulDice));

        assertEquals(0, state.getTurnCount());
        assertTrue(state.hasPendingDecision());

        engine.resolveMemoryDecision(PlayerAttribute.COMPOSURE);

        assertEquals(1, state.getTurnCount());

        assertFalse(state.hasPendingDecision());

        assertEquals(5, player.getStats().getComposure());
    }
}