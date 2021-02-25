package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Represents a train station
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public class Station {
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


    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
