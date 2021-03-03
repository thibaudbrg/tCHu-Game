package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Represents a train station
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Station {
    private final int id;
    private final String name;

    /**
     * Built a train station
     *
     * @param id   (int) the id of the train station
     * @param name (String) the name of the train station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }

    /**
     * Return the id of the Station
     *
     * @return the id of the Station
     */
    public int id() {
        return id;
    }

    /**
     * Return rhe name of the Station
     *
     * @return the name of the Station
     */
    public String name() {
        return name;
    }

    /**
     * Write the name of the Station
     *
     * @return the String of the name of the Station
     */
    @Override
    public String toString() {
        return name;
    }
}