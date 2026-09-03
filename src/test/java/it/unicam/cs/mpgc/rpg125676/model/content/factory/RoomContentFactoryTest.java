package it.unicam.cs.mpgc.rpg125676.model.content.factory;

import it.unicam.cs.mpgc.rpg125676.model.content.ContentType;
import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomContentFactoryTest {

    private List<RoomContent> contents;

    @BeforeEach
    void setUp() {
        GameSettings settings = GameSettings.standard();
        RoomContentFactory factory = new RoomContentFactory(settings.content());
        contents = factory.createContents();
    }

    @Test
    void shouldCreateNineContents() {
        assertEquals(9, contents.size());
    }

    @Test
    void shouldCreateThreeMemories() {
        assertEquals(3, count(ContentType.MEMORY));
    }

    @Test
    void shouldCreateTwoMedicines() {
        assertEquals(2, count(ContentType.MEDICINE));
    }

    @Test
    void shouldCreateTwoDecoys() {
        assertEquals(2, count(ContentType.DECOY));
    }

    @Test
    void shouldCreateTwoEmptyContents() {
        assertEquals(2, count(ContentType.EMPTY));
    }

    private long count(ContentType type) {
        return contents
                .stream()
                .filter(
                        content -> content.getType() == type
                )
                .count();
    }
}