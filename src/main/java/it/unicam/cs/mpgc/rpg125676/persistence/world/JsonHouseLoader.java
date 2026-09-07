package it.unicam.cs.mpgc.rpg125676.persistence.world;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;
import it.unicam.cs.mpgc.rpg125676.persistence.world.dto.HouseData;
import it.unicam.cs.mpgc.rpg125676.persistence.world.dto.RoomData;
import it.unicam.cs.mpgc.rpg125676.persistence.PersistenceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Loads the structure of the game house from a JSON resource.
 * The loader deserializes the resource into persistence data objects,
 * validates the required room information and builds the corresponding
 * domain {@link House} with its rooms and connections.
 * Persistence-related failures are reported through
 * {@link PersistenceException}.
 */
public class JsonHouseLoader implements HouseLoader {

    private final Gson gson;
    private final String resourcePath;

    public JsonHouseLoader(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("Resource path cannot be blank");
        }
        this.gson = new Gson();
        this.resourcePath = resourcePath;
    }

    /**
     * Loads and builds the house described by the configured JSON resource.
     *
     * @return the house created from the resource data
     * @throws PersistenceException if the resource cannot be found,
     *         read, parsed or contains invalid house data
     */
    @Override
    public House load() {
        try (
                InputStream inputStream = openResource();

                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        ) {
            HouseData houseData = gson.fromJson(reader, HouseData.class);
            validateHouseData(houseData);
            return buildHouse(houseData);

        } catch (IOException | JsonParseException exception) {
            throw new PersistenceException("Unable to load house from " + resourcePath, exception);
        }
    }

    /**
     * Opens the configured house resource from the application classpath.
     *
     * @return input stream associated with the house resource
     * @throws PersistenceException if the resource cannot be found
     */
    private InputStream openResource() {
        InputStream inputStream = JsonHouseLoader.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new PersistenceException("House resource not found: " + resourcePath);
        }
        return inputStream;
    }

    /**
     * Builds the domain house from the supplied persistence data.
     * Rooms are created before their connections are established.
     *
     * @param houseData validated house data
     * @return the constructed house
     */
    private House buildHouse(HouseData houseData) {
        DefaultHouse house = new DefaultHouse();
        addRooms(house, houseData);
        addConnections(house, houseData);
        return house;
    }

    /**
     * Creates and adds all rooms described by the supplied house data.
     *
     * @param house house receiving the created rooms
     * @param houseData source data containing the room definitions
     * @throws PersistenceException if a room contains invalid required data
     */
    private void addRooms(DefaultHouse house, HouseData houseData) {
        for (RoomData roomData : houseData.rooms()) {
            validateRoomData(roomData);
            Room room = new Room(roomData.id(), roomData.name(), roomData.searchable(), roomData.role(), roomData.presenceStartAllowed());
            house.addRoom(room);
        }
    }

    /**
     * Creates the connections between the rooms of the supplied house.
     *
     * @param house house whose room connections must be created
     * @param houseData source data containing the connection definitions
     */
    private void addConnections(DefaultHouse house, HouseData houseData) {
        for (RoomData roomData : houseData.rooms()) {
            if (roomData.connections() == null) {
                continue;
            }

            for (String connectedRoomId : roomData.connections()) {
                house.connectRooms(roomData.id(), connectedRoomId);
            }
        }
    }

    /**
     * Validates the basic structure of the loaded house data.
     *
     * @param houseData house data to validate
     * @throws PersistenceException if the house data is null
     *         or does not contain any rooms
     */
    private void validateHouseData(HouseData houseData) {
        if (houseData == null || houseData.rooms() == null || houseData.rooms().isEmpty()) {
            throw new PersistenceException("House data does not contain rooms");
        }
    }

    /**
     * Validates the required information of a room definition.
     *
     * @param roomData room data to validate
     * @throws PersistenceException if the room data is null or if
     *         its identifier, name or role is invalid
     */
    private void validateRoomData(RoomData roomData) {
        if (roomData == null) {throw new PersistenceException("House contains an invalid room");
        }
        if (roomData.id() == null || roomData.id().isBlank()) {
            throw new PersistenceException("Room id cannot be blank");
        }
        if (roomData.name() == null || roomData.name().isBlank()) {
            throw new PersistenceException("Room name cannot be blank");
        }

        if (roomData.role() == null) {
            throw new PersistenceException("Room role cannot be null: " + roomData.id());
        }
    }
}
