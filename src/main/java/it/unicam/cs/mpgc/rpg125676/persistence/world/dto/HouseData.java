package it.unicam.cs.mpgc.rpg125676.persistence.world.dto;

import java.util.List;

/**
 * Data Transfer Object used to deserialize the house from JSON.
 */
public record HouseData(List<RoomData> rooms) {}