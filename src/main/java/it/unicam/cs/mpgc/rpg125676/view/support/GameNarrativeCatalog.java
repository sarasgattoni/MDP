package it.unicam.cs.mpgc.rpg125676.view.support;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.Map;

/**
 * Contains narrative text used during the game.
 */
public class GameNarrativeCatalog {

    private final Map<String, String> roomDescriptions;

    public GameNarrativeCatalog() {
        roomDescriptions = Map.ofEntries(

                Map.entry("entrance", "Cold air slips through the broken doorway. " + "The house feels less abandoned than it should."),
                Map.entry("hallway", "Faded portraits watch the corridor. Their faces have been scratched away." ),
                Map.entry("kitchen", "Dust covers the counters, but a rusted knife still lies beside the sink." ),
                Map.entry("pantry", "Empty shelves lean beneath the weight of damp wood. Something has disturbed the dust." ),
                Map.entry("living_room", "A collapsed sofa faces a cold fireplace. Family photographs lie face down on the floor." ),
                Map.entry("study", "Books rot inside warped shelves. A desk drawer has been forced open." ),
                Map.entry("stairs", "Each step complains beneath your weight. The darkness above seems deeper than the hallway behind you." ),
                Map.entry("bathroom", "The mirror is fractured from the inside out. Dark stains circle the drain." ),
                Map.entry("parents_bedroom", "The bed remains perfectly made. Two empty spaces mark where photographs once hung." ),
                Map.entry("child_bedroom", "Faded stars cover the ceiling. A music box sits open beside the small bed." ),
                Map.entry("attic", "Sheets cover forgotten furniture. Something has carved lines into the wooden floor." ),
                Map.entry("locked_room", "The air is colder here. This is where the house has been waiting for you." )
        );
    }

    public String roomDescription(Room room) {
        return roomDescriptions.getOrDefault(room.getId(), "The room offers no comfort.");
    }

    public String memoryTitle(int number) {
        return switch (number) {
            case 1 -> "MEMORY I — THE PHOTOGRAPH";
            case 2 -> "MEMORY II — THE NIGHT";
            case 3 -> "MEMORY III — HER NAME";
            default -> "RECOVERED MEMORY";
        };
    }

    public String memoryText(int number) {
        return switch (number) {

            case 1 ->
                    """
                    A torn photograph lies beneath the dust.

                    A family stands outside this house. Two parents,
                    a young child, and an older girl whose face has
                    been violently scratched away.

                    On the back, only a few words remain:

                    "Summer, 1987. Before everything changed."
                    """;

            case 2 ->
                    """
                    The memory does not feel like yours.

                    You hear shouting downstairs. A door slams.
                    Someone is crying behind a locked door.

                    A man's voice says:

                    "She must never leave this room."

                    Then comes the smell of smoke.
                    """;

            case 3 ->
                    """
                    The final fragment completes the photograph.

                    The scratched-out girl is standing alone.
                    Someone has written a name beneath her face.

                    ELEANOR.

                    She did not haunt this house.

                    She died inside it.

                    And the house never stopped listening for her.
                    """;

            default ->
                    "A fragment of the house's past returns to you.";
        };
    }
}
