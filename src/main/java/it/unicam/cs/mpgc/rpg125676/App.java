package it.unicam.cs.mpgc.rpg125676;

import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.factory.GameFactory;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;
import it.unicam.cs.mpgc.rpg125676.persistence.leaderboard.JsonLeaderboardRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.leaderboard.LeaderboardRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.save.SaveRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.save.SerializedSaveRepository;
import it.unicam.cs.mpgc.rpg125676.persistence.world.JsonHouseLoader;
import it.unicam.cs.mpgc.rpg125676.controller.service.GameSession;
import it.unicam.cs.mpgc.rpg125676.view.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        GameSettings settings = GameSettings.standard();
        HouseLoader houseLoader = new JsonHouseLoader("/data/house.json");
        GameFactory gameFactory = new GameFactory(settings, houseLoader);
        SaveRepository saveRepository = new SerializedSaveRepository(Path.of("data", "savegame.bin"));
        LeaderboardRepository leaderboardRepository = new JsonLeaderboardRepository(Path.of("data", "leaderboard.json"));
        GameSession session = new GameSession(gameFactory, saveRepository, leaderboardRepository);
        SceneNavigator navigator = new SceneNavigator(stage, session);
        navigator.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}