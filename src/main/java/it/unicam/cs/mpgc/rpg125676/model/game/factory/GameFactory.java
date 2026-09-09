package it.unicam.cs.mpgc.rpg125676.model.game.factory;

import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;
import it.unicam.cs.mpgc.rpg125676.model.content.distribution.ContentDistributor;
import it.unicam.cs.mpgc.rpg125676.model.content.factory.RoomContentFactory;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.DecoyAwarePresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceStateResolver;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.ShortestPathPresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.AttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.StandardAttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.end.GameEndEvaluator;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.DefaultGameEngine;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnProcessor;
import it.unicam.cs.mpgc.rpg125676.model.movement.BfsPathFinder;
import it.unicam.cs.mpgc.rpg125676.model.movement.PathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Creates and wires all objects required for a new game session.
 *
 * <p>This class acts as a composition point for the game model.
 * The individual domain classes do not need to know how the
 * complete object graph is assembled.</p>
 */
public class GameFactory {

    private final GameSettings settings;
    private final HouseLoader houseLoader;
    private final Random random;

    /**
     * Creates a game factory using the supplied settings and house loader.
     *
     * A random generator is created internally and used during
     * game initialization.
     *
     * @param settings configuration used to create game sessions
     * @param houseLoader loader used to create the game house
     * @throws NullPointerException if settings or houseLoader is null
     */
    public GameFactory(GameSettings settings, HouseLoader houseLoader) {
        this.settings = Objects.requireNonNull(settings);
        this.houseLoader = Objects.requireNonNull(houseLoader);
        this.random = new Random();
    }

    /**
     * Creates a completely initialized new game session.
     * The house is loaded and populated with room contents, the starting
     * rooms of the player and the Presence are determined, and the
     * corresponding game entities are created.
     * The resulting state is then wrapped in a ready-to-use game engine.
     *
     * @param playerName name selected by the player
     * @return a game engine containing the newly initialized session
     */
    public GameEngine createNewGame(String playerName) {
        House house = houseLoader.load();
        distributeRoomContents(house);
        Room playerStartRoom = findPlayerStartRoom(house);
        Room presenceStartRoom = choosePresenceStartRoom(house, playerStartRoom);
        Player player = createPlayer(playerName, playerStartRoom);
        Presence presence = createPresence(presenceStartRoom);
        GameState state = new GameState(settings, house, player, presence);

        return createEngineFor(state);
    }

    /**
     * Creates and distributes the configured room contents
     * throughout the searchable rooms of the house.
     *
     * @param house house whose rooms must receive their contents
     */
    private void distributeRoomContents(House house) {
        RoomContentFactory contentFactory = new RoomContentFactory(settings.content());
        List<RoomContent> contents = contentFactory.createContents();
        ContentDistributor distributor = new ContentDistributor(random);
        distributor.distribute(house, contents);
    }

    /**
     * Finds the unique room configured as the player's starting room.
     *
     * @param house house in which the starting room is searched
     * @return the unique player starting room
     * @throws IllegalStateException if the house does not contain
     *         exactly one start room
     */
    private Room findPlayerStartRoom(House house) {
        List<Room> startRooms = house.getRooms().stream().filter(room -> room.getRole() == RoomRole.START).toList();
        if (startRooms.size() != 1) {
            throw new IllegalStateException("The house must contain exactly one start room");
        }

        return startRooms.getFirst();
    }

    /**
     * Selects a valid starting room for the Presence.
     * The room is chosen randomly among the rooms that allow the Presence
     * to start there, excluding the player's starting room.
     *
     * @param house house containing the candidate rooms
     * @param playerStartRoom room in which the player starts
     * @return the selected Presence starting room
     * @throws IllegalStateException if no valid starting room is available
     */
    private Room choosePresenceStartRoom(House house, Room playerStartRoom) {
        List<Room> candidates = house.getRooms().stream().filter(Room::isPresenceStartAllowed).filter(room -> !room.equals(playerStartRoom)).toList();
        if (candidates.isEmpty()) {
            throw new IllegalStateException("The house does not contain " + "a valid Presence starting room");
        }
        int index = random.nextInt(candidates.size());

        return candidates.get(index);
    }

    /**
     * Creates the player with the statistics and inventory required
     * for a new game session.
     * The initial statistics and total number of required memories
     * are obtained from game settings.
     *
     * @param playerName name of the player
     * @param startingRoom room in which the player starts
     * @return the initialized player
     */
    private Player createPlayer(String playerName, Room startingRoom) {
        PlayerStats stats = new PlayerStats(settings.player());
        PlayerInventory inventory = new PlayerInventory();

        return new Player(playerName, stats, inventory, startingRoom, settings.content().memoryCount());
    }

    /**
     * Creates the Presence in the supplied starting room.
     * Its initial attention limits are obtained from game configuration.
     *
     * @param startingRoom room in which the Presence starts
     * @return the initialized Presence
     */
    private Presence createPresence(Room startingRoom) {
        return new Presence(startingRoom, settings.presence().minAttention(), settings.presence().maxAttention());
    }

    /**
     * Creates a game engine for an existing game state.
     * The method assembles the path-finding, Presence movement,
     * attention, end-game and turn-processing components required
     * to continue the session.
     *
     * @param state game state to associate with the engine
     * @return a game engine ready to continue the supplied session
     * @throws NullPointerException if state is null
     */
    public GameEngine createEngineFor(GameState state){
        Objects.requireNonNull(state);
        PathFinder pathFinder = new BfsPathFinder();
        PresenceStateResolver stateResolver = new PresenceStateResolver(state.getSettings().presence());
        PresenceMovementStrategy standardMovement = new ShortestPathPresenceMovementStrategy(pathFinder, stateResolver);
        PresenceMovementStrategy movement = new DecoyAwarePresenceMovementStrategy(standardMovement);
        AttentionPolicy attentionPolicy = new StandardAttentionPolicy();
        GameEndEvaluator endEvaluator = new GameEndEvaluator();
        TurnProcessor turnProcessor = new TurnProcessor(state, attentionPolicy, movement, endEvaluator);

        return new DefaultGameEngine(state, turnProcessor);
    }
}
