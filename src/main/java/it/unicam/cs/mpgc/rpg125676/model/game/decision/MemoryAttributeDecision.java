package it.unicam.cs.mpgc.rpg125676.model.game.decision;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;

import java.util.Objects;

/**
 * Represents the attribute improvement decision generated after
 * discovering a memory.
 * The decision allows the player to choose one available attribute
 * and increase it by the configured bonus value.
 */
public class MemoryAttributeDecision implements PendingDecision {

    private final int bonus;

    /**
     * Creates a memory attribute decision with the specified bonus.
     *
     * @param bonus amount added to the selected attribute
     * @throws IllegalArgumentException if bonus is not positive
     */
    public MemoryAttributeDecision(int bonus) {
        if (bonus <= 0) {throw new IllegalArgumentException("Memory attribute bonus must be positive");}
        this.bonus = bonus;
    }

    /**
     * Returns the type associated with this decision.
     *
     * @return {@link DecisionType#MEMORY_ATTRIBUTE}
     */
    @Override
    public DecisionType getType() {
        return DecisionType.MEMORY_ATTRIBUTE;
    }

    /**
     * Returns the message presented to the player when the decision
     * must be resolved.
     *
     * @return the decision prompt
     */
    @Override
    public String getPrompt() {
        return "Choose which attribute to improve: "
                + "Composure or Caution.";
    }

    /**
     * Returns the bonus applied when the decision is resolved.
     *
     * @return the attribute bonus
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Resolves the decision by increasing the selected player attribute.
     * The configured bonus is applied to the chosen attribute through
     * the player's statistics.
     * @param player player whose attribute is improved
     * @param attribute attribute selected by the player
     * @throws NullPointerException if player or attribute is null
     */
    public void resolve(Player player, PlayerAttribute attribute) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(attribute);

        player
                .getStats()
                .increaseAttribute(attribute, bonus);
    }
}