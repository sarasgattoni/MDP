package it.unicam.cs.mpgc.rpg125676.command;

/**
 * Represents the outcome of executing a game action.
 */
public enum ActionOutcome {
    /**
     * The action was executed successfully.
     */
    SUCCESS,
    /**
     * The action was executed but did not succeed.
     */
    FAILURE,
    /**
     * The action could not be executed.
     */
    REJECTED
}
