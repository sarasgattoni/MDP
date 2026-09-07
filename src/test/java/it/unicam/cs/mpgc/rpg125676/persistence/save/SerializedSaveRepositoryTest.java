package it.unicam.cs.mpgc.rpg125676.persistence.save;

import it.unicam.cs.mpgc.rpg125676.command.exploration.MoveAction;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.game.factory.GameFactory;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;
import it.unicam.cs.mpgc.rpg125676.persistence.world.JsonHouseLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SerializedSaveRepositoryTest {

    @TempDir
    Path tempDirectory;

    private GameFactory gameFactory;
    private GameEngine originalEngine;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        HouseLoader houseLoader = new JsonHouseLoader("/data/house.json");
        gameFactory = new GameFactory(settings, houseLoader);
        originalEngine = gameFactory.createNewGame("Test Player");
    }

    @Test
    void shouldSaveAndLoadGameState() {
        GameState originalState = originalEngine.getState();

        Room hallway = originalState.getHouse().getRoom("hallway");
        originalEngine.execute(new MoveAction(hallway));
        SaveRepository repository = createRepository();
        repository.save(originalState);

        assertTrue(repository.exists());

        GameState loadedState = repository.load();

        assertEquals(originalState.getPlayer().getName(), loadedState.getPlayer().getName());
        assertEquals(originalState.getPlayer().getCurrentRoom().getId(), loadedState.getPlayer().getCurrentRoom().getId());
        assertEquals(originalState.getPresence().getCurrentRoom().getId(), loadedState.getPresence().getCurrentRoom().getId());
        assertEquals(originalState.getPresence().getAttention(), loadedState.getPresence().getAttention());
        assertEquals(originalState.getTurnCount(), loadedState.getTurnCount());
    }

    @Test
    void serializationShouldPreserveRoomReferences() {
        SaveRepository repository = createRepository();
        repository.save(originalEngine.getState());
        GameState loadedState = repository.load();
        Room playerRoom = loadedState.getPlayer().getCurrentRoom();
        Room houseRoom = loadedState.getHouse().getRoom(playerRoom.getId());

        assertSame(houseRoom, playerRoom);
    }

    @Test
    void loadedStateShouldSupportNewGameEngine() {
        SaveRepository repository = createRepository();
        repository.save(originalEngine.getState());
        GameState loadedState = repository.load();
        GameEngine loadedEngine = gameFactory.createEngineFor(loadedState);
        Room hallway = loadedState.getHouse().getRoom("hallway");
        loadedEngine.execute(new MoveAction(hallway));

        assertEquals(hallway, loadedState.getPlayer().getCurrentRoom());
        assertEquals(1, loadedState.getTurnCount());
    }

    @Test
    void shouldDeleteSavedGame() {
        SaveRepository repository = createRepository();
        repository.save(originalEngine.getState());

        assertTrue(repository.exists());

        repository.delete();

        assertFalse(repository.exists());
    }

    private SaveRepository createRepository() {
        return new SerializedSaveRepository(tempDirectory.resolve("savegame.bin"));
    }
}