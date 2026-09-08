package it.unicam.cs.mpgc.rpg125676.controller.service;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.game.factory.GameFactory;
import it.unicam.cs.mpgc.rpg125676.model.leaderboard.LeaderboardEntry;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import it.unicam.cs.mpgc.rpg125676.persistence.leaderboard.LeaderboardRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.save.SaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private GameState state;
    private TestSaveRepository saveRepository;
    private TestLeaderboardRepository leaderboardRepository;
    private GameSession session;

    @BeforeEach
    void setUp() {
        state = createState();
        saveRepository = new TestSaveRepository();
        leaderboardRepository = new TestLeaderboardRepository();

        GameFactory gameFactory = new TestGameFactory(state);
        session = new GameSession(gameFactory, saveRepository, leaderboardRepository);
    }

    @Test
    void shouldRejectAccessWhenNoGameIsActive() {
        assertThrows(IllegalStateException.class, session::getCurrentGame);
    }

    @Test
    void shouldStartNewGame() {
        GameEngine engine = session.startNewGame("Sara");

        assertSame(engine, session.getCurrentGame());
        assertSame(state, engine.getState());
    }

    @Test
    void shouldSaveCurrentGame() {
        session.startNewGame("Sara");
        session.saveCurrentGame();

        assertTrue(saveRepository.exists());
        assertSame(state, saveRepository.load());
    }

    @Test
    void shouldLoadSavedGame() {
        saveRepository.save(state);

        GameEngine engine = session.loadSavedGame();

        assertSame(engine, session.getCurrentGame());
        assertSame(state, engine.getState());
    }

    @Test
    void shouldRecordVictoryOnlyOnce() {
        session.startNewGame("Sara");
        state.advanceTurn();
        state.win();
        session.completeCurrentGame();
        session.completeCurrentGame();

        assertEquals(1, leaderboardRepository.findAll().size());
        assertFalse(saveRepository.exists());
    }

    @Test
    void shouldNotRecordLoss() {
        session.startNewGame("Sara");
        state.advanceTurn();
        state.lose();
        session.completeCurrentGame();

        assertTrue(leaderboardRepository.findAll().isEmpty());
    }

    private GameState createState() {
        GameSettings settings = GameSettings.standard();

        Room entrance = new Room("entrance", "Entrance", false, RoomRole.START, false);
        Room attic = new Room("attic", "Attic", false, RoomRole.STANDARD, true);
        DefaultHouse house = new DefaultHouse();
        house.addRoom(entrance);
        house.addRoom(attic);
        house.connectRooms("entrance", "attic");
        Player player = new Player("Sara", new PlayerStats(settings.player()), new PlayerInventory(), entrance, settings.content().memoryCount());
        Presence presence = new Presence(attic, settings.presence().minAttention(), settings.presence().maxAttention());

        return new GameState(settings, house, player, presence);
    }

    private static class TestGameFactory extends GameFactory {

        private final GameState state;

        TestGameFactory(GameState state) {
            super(GameSettings.standard(), () -> {throw new UnsupportedOperationException();});
            this.state = state;
        }

        @Override
        public GameEngine createNewGame(String playerName) {
            return new TestGameEngine(state);
        }

        @Override
        public GameEngine createEngineFor(GameState state) {
            return new TestGameEngine(state);
        }
    }

    private record TestGameEngine(GameState state) implements GameEngine {

        @Override
        public GameState getState() {
            return state;
        }

        @Override
        public ActionResult execute(GameAction action) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String resolveMemoryDecision(PlayerAttribute attribute) {
            throw new UnsupportedOperationException();
        }
    }

    private static class TestSaveRepository implements SaveRepository {

        private GameState state;

        @Override
        public boolean exists() {
            return state != null;
        }

        @Override
        public void save(GameState state) {
            this.state = state;
        }

        @Override
        public GameState load() {
            return state;
        }

        @Override
        public void delete() {
            state = null;
        }
    }

    private static class TestLeaderboardRepository implements LeaderboardRepository {

        private final List<LeaderboardEntry> entries = new ArrayList<>();

        @Override
        public List<LeaderboardEntry> findAll() {
            return List.copyOf(entries);
        }

        @Override
        public void add(LeaderboardEntry entry) {
            entries.add(entry);
        }

        @Override
        public void clear() {
            entries.clear();
        }
    }
}