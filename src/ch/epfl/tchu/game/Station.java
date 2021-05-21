package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

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
        this.name = Objects.requireNonNull(name);

    }

    /**
     * Return the id of the Station
     *
     * @return (int) the id of the Station
     */
    public int id() {
        return id;
    }

    /**
     * Return rhe name of the Station
     *
     * @return (String) the name of the Station
     */
    public String name() {
        return name;
    }

    /**
     * Write the name of the Station
     *
     * @return (String) the String of the name of the Station
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Indicates whether some other station is "equal to" this one
     *
     * @param o (Object) The station
     * @return (boolean) Return true if the two stations are equals
     */
    /*
   @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id == station.id
                && name.equals(station.name);
    }
*/

    /**
     * Returns a hash code value for the object. (Redefined because of equals())
     *
     * @return (int) The hashcode
     */
    /*
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
*/}