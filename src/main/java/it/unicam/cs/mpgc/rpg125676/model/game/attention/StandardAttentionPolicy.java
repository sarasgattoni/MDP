package it.unicam.cs.mpgc.rpg125676.model.game.attention;

import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnNoise;

import java.util.Objects;

/**
 * Applies the standard attention rules defined by the game.
 * During a silent turn, the Presence attention is reduced according
 * to the configured value. When noise is generated, attention is
 * increased by the total noise amount and the latest noise source
 * becomes the current target of the Presence.
 */
public class StandardAttentionPolicy implements AttentionPolicy {

    /**
     * Applies the attention rule associated with the supplied turn noise.
     * Silent turns decrease Presence attention, while noisy turns
     * increase it and update the current noise target.
     *
     * @param state current game state
     * @param noise total noise generated during the turn
     * @throws NullPointerException if state or noise is null
     */
    @Override
    public void apply(GameState state, TurnNoise noise) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(noise);
        Presence presence = state.getPresence();
        if (noise.amount() == 0) {
            presence.decreaseAttention(state.getSettings().noise().silentTurnAttentionDecrease());
            return;
        }
        presence.increaseAttention(noise.amount());
        noise.lastSource().ifPresent(state::setNoiseTarget);
    }
}