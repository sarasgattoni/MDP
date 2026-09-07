package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionOutcome;
import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.content.Decoy;
import it.unicam.cs.mpgc.rpg125676.model.content.EmptyContent;
import it.unicam.cs.mpgc.rpg125676.model.content.Memory;
import it.unicam.cs.mpgc.rpg125676.model.content.Medicine;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerStats;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.inventory.PlayerInventory;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decision.DecisionType;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchActionTest {

    private GameSettings settings;
    private Room searchableRoom;
    private Room presenceRoom;
    private Player player;
    private GameState state;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        searchableRoom = new Room("study", "Study", true, RoomRole.STANDARD, false);

        presenceRoom = new Room("attic", "Attic", true, RoomRole.STANDARD, false);
        DefaultHouse house = new DefaultHouse();

        house.addRoom(searchableRoom);
        house.addRoom(presenceRoom);
        house.connectRooms("study", "attic");

        player = new Player("Test Player", new PlayerStats(settings.player()), new PlayerInventory(), searchableRoom, settings.content().memoryCount());
        Presence presence = new Presence(presenceRoom, settings.presence().minAttention(), settings.presence().maxAttention());
        state = new GameState(settings, house, player, presence);
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
    void successfulCautionCheckShouldMakeSearchSilent() {
        searchableRoom.setContent(new EmptyContent("Nothing."));
        DiceRoller dice = fixedDice(6);
        SearchAction action = new SearchAction(dice);
        ActionResult result = action.execute(state);
        
        assertEquals(ActionOutcome.SUCCESS, result.outcome());
        assertEquals(0, result.noise());
        assertTrue(searchableRoom.isSearched());
    }

    @Test
    void failedCautionCheckShouldGenerateSearchNoise() {
        searchableRoom.setContent(new EmptyContent("Nothing."));
        DiceRoller dice =  fixedDice(1);
        SearchAction action = new SearchAction(dice);
        ActionResult result = action.execute(state);

        assertEquals(settings.noise().searchNoise(), result.noise());
        assertEquals(searchableRoom, result.noiseSource().orElseThrow());
    }

    @Test
    void roomShouldNotBeSearchableTwice() {
        searchableRoom.setContent(new EmptyContent("Nothing."));
        DiceRoller dice =  fixedDice(6);
        SearchAction action = new SearchAction(dice);
        action.execute(state);
        ActionResult secondResult = action.execute(state);

        assertEquals(ActionOutcome.REJECTED, secondResult.outcome());
        assertFalse(secondResult.consumesTurn());
    }

    @Test
    void medicineShouldRestoreLucidityImmediately() {
        player.getStats().loseLucidity(4);
        searchableRoom.setContent(new Medicine("Medicine found."));
        DiceRoller dice =  fixedDice(6);
        new SearchAction(dice).execute(state);

        assertEquals(8, player.getStats().getLucidity());
    }

    @Test
    void memoryShouldRegisterMemoryAndRequestAttributeDecision() {
        searchableRoom.setContent(new Memory(1, "A forgotten memory."));
        int initialComposure = player.getStats().getComposure();
        int initialCaution = player.getStats().getCaution();
        DiceRoller dice =  fixedDice(6);
        new SearchAction(dice).execute(state);

        assertEquals(1, player.getMemoriesFound());
        assertTrue(state.hasPendingDecision());
        assertEquals(DecisionType.MEMORY_ATTRIBUTE, state.getPendingDecision().orElseThrow().getType());
        assertEquals(initialComposure, player.getStats().getComposure());
        assertEquals(initialCaution, player.getStats().getCaution());
    }

    @Test
    void anotherActionShouldBeRejectedWhileDecisionIsPending() {
        searchableRoom.setContent(new Memory(1, "A forgotten memory."));
        DiceRoller dice =  fixedDice(6);
        new SearchAction(dice).execute(state);
        ListenAction listen = new ListenAction();
        ActionResult result = listen.execute(state);

        assertEquals(ActionOutcome.REJECTED, result.outcome());
        assertFalse(result.consumesTurn()
        );
    }
    @Test
    void decoyShouldBeAddedToInventory() {
        searchableRoom.setContent(new Decoy("An old alarm clock."));
        DiceRoller dice =  fixedDice(6);
        new SearchAction(dice).execute(state);

        assertEquals(1, player.getDecoyCount());
        assertTrue(player.hasDecoy());
    }
}