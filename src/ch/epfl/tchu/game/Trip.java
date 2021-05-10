package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Trip
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Build a Trip
     *
     * @param from   (Station) departure station
     * @param to     (Station) arrival station
     * @param points (int) number of points given to the trip
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;

    }

    /**
     * Creates a list of all possible trip between a list of departure station and arrival
     * station and give a number of points to the trip
     *
     * @param from   (Station) departure station
     * @param to     (Station) arrival station
     * @param points (int) number of points given to the trip
     * @return (List < Trip >) returns a list of all possible Trip between the given departure and arrival stations
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument((!(from.isEmpty() && to.isEmpty())));

        List<Trip> allList = new ArrayList<>();
        for (Station stationFrom : from) {
            for (Station stationTo : to) {
                allList.add(new Trip(stationFrom, stationTo, points));
            }

        }
        return allList;
    }

    /**
     * Return the departure Station
     *
     * @return (Station) the departure Station
     */
    public Station from() {
        return from;
    }

    /**
     * Return the arrival Station
     *
     * @return (Station) the arrival Station
     */
    public Station to() {
        return to;
    }

    /**
     * Return the number of points given to the trip
     *
     * @return (int) the number of points given to the trip
     */
    public int points() {
        return points;
    }

    /**
     * Returns the number of points on the route if the connected method returns true when applied
     * to the two stations on the route - meaning that they are well connected -, and the negation
     * of this number of points otherwise
     *
     * @param connectivity (StationConnectivity) the connexion
     * @return (int) The number of points in the path for the given connectivity
     */
    public int points(StationConnectivity connectivity) {

        return (connectivity.connected(from, to)) ? points : -points;
    }

    /**
     * Indicates whether some other trip is "equal to" this one
     *
     * @param o (Object) The trip
     * @return (boolean) Return true if the two trips are equals
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return points == trip.points
                && from.equals(trip.from)
                && to.equals(trip.to);
    }

    /**
     * Returns a hash code value for the object. (Redefined because of equals())
     *
     * @return (int) The hashcode
     */

    @Override
    public int hashCode() {
        return Objects.hash(from, to, points);
    }
}
