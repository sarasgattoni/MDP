package it.unicam.cs.mpgc.rpg125676.persistence;

/**
 * Signals an error occurring while reading or writing
 * persistent application data.
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
