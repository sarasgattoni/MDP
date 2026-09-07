package it.unicam.cs.mpgc.rpg125676.persistence.world.dto;

import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;

import java.util.List;

/**
 * Data Transfer Object used to deserialize room data from JSON.
 */
public record RoomData(String id, String name, boolean searchable, RoomRole role, boolean presenceStartAllowed, List<String> connections) {}
