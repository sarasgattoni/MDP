package it.unicam.cs.mpgc.rpg125676.model.content;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.Objects;

/**
 * Represents medicine that restores the player's lucidity when discovered.
 * The amount of lucidity restored is obtained from the current
 * game configuration. The resulting lucidity cannot exceed the
 * maximum value allowed for the player.
 */
public class Medicine extends AbstractRoomContent {

    /**
     * Creates medicine with the specified discovery description.
     *
     * @param description description shown when the medicine is discovered
     * @throws IllegalArgumentException if description is null or blank
     */
    public Medicine(String description) {
        super(ContentType.MEDICINE, "Medicine", description);
    }
    /**
     * Applies the effect of discovering the medicine.
     * The player's lucidity is restored by the amount specified in
     * the current game configuration. The discovery is then completed
     * without requiring an additional decision.
     * @param player player discovering the medicine
     * @param settings current game configuration
     * @return the completed discovery result
     * @throws NullPointerException if player or settings is null
     */
    @Override
    public ContentDiscovery discover(Player player, GameSettings settings) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(settings);

        int healing =
                settings
                        .player()
                        .medicineHealing();
        player
                .getStats()
                .recoverLucidity(healing);

        return ContentDiscovery.completed(getDescription() + " You recovered " + healing + " lucidity.");
    }

}