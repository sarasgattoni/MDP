package it.unicam.cs.mpgc.rpg125676.model.world.loading;

import it.unicam.cs.mpgc.rpg125676.model.world.House;

/**
 * Defines a component capable of loading a house.
 */
public interface HouseLoader {

    /**
     * Loads and returns a house.
     *
     * @return loaded house
     */
    House load();
}