package it.unicam.cs.mpgc.rpg125676.model.game.engine;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

/**
 * Defines the main interface used to interact with a running game session.
 * The engine provides access to the current game state, executes
 * player actions and coordinates the resolution of decisions that
 * temporarily suspend the completion of a turn.
 */
public interface GameEngine {
    GameState getState();
    ActionResult execute(GameAction action);
    String resolveMemoryDecision(PlayerAttribute attribute);
}
