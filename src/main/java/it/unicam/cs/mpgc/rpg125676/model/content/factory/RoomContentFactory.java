package it.unicam.cs.mpgc.rpg125676.model.content.factory;

import it.unicam.cs.mpgc.rpg125676.model.content.Decoy;
import it.unicam.cs.mpgc.rpg125676.model.content.EmptyContent;
import it.unicam.cs.mpgc.rpg125676.model.content.Medicine;
import it.unicam.cs.mpgc.rpg125676.model.content.Memory;
import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Creates the room contents required for a game session.
 * The number of memories, medicines, decoys and empty contents
 * is determined by the supplied content configuration.
 * The factory is responsible only for creating the content objects.
 */
public class RoomContentFactory {

    private final GameSettings.ContentConfig config;

    /**
     * Creates a factory using the specified content configuration.
     *
     * @param config configuration that defines how many contents of each type
     *               must be created
     * @throws NullPointerException if config is null
     */
    public RoomContentFactory(GameSettings.ContentConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Creates the complete collection of room contents for a game session.
     *
     * The returned list contains all configured memories, medicines,
     * decoys and empty contents.
     *
     * @return a list containing all generated room contents
     */
    public List<RoomContent> createContents() {
        List<RoomContent> contents = new ArrayList<>();

        addMemories(contents);
        addMedicines(contents);
        addDecoys(contents);
        addEmptyContents(contents);

        return contents;
    }

    /**
     * Adds the configured memories to the supplied content list.
     *
     * @param contents list receiving the generated memories
     */
    private void addMemories(List<RoomContent> contents) {
        for (int i = 1; i <= config.memoryCount(); i++) {
            contents.add(new Memory(i, memoryDescription(i))
            );
        }
    }

    /**
     * Adds the configured medicines to the supplied content list.
     *
     * @param contents list receiving the generated medicines
     */
    private void addMedicines(List<RoomContent> contents) {
        for (int i = 0; i < config.medicineCount(); i++) {

            contents.add(new Medicine("You found medicine left " + "inside an old cabinet."));
        }
    }

    /**
     * Adds the configured decoys to the supplied content list.
     *
     * @param contents list receiving the generated decoys
     */
    private void addDecoys(List<RoomContent> contents) {
        for (int i = 0; i < config.decoyCount(); i++) {
            contents.add(new Decoy("You found an old alarm clock " + "that can be used as a decoy."));
        }
    }

    /**
     * Adds the configured empty contents to the supplied content list.
     *
     * @param contents list receiving the generated empty contents
     */
    private void addEmptyContents(List<RoomContent> contents) {
        for (int i = 0; i < config.emptyContentCount(); i++) {
            contents.add(new EmptyContent("You searched carefully, " + "but found nothing useful."));
        }
    }

    /**
     * Returns the narrative description associated with a memory sequence.
     *
     * @param sequenceNumber sequence number of the memory
     * @return the corresponding memory description
     */
    private String memoryDescription(int sequenceNumber) {
        return switch (sequenceNumber) {
            case 1 ->
                    "A torn photograph reveals "
                            + "the first fragment of the past.";

            case 2 ->
                    "A damaged personal note reveals "
                            + "another fragment of the past.";

            case 3 ->
                    "The final memory reveals "
                            + "the identity of the Presence.";

            default ->
                    "A fragment of the past resurfaces.";
        };
    }
}