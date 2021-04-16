package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Represents a ticket
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Ticket implements Comparable<Ticket> {
    private final String Text;
    private final Station stationFrom;
    private final List<Trip> trips;

    /**
     * Built a ticket
     *
     * @param trips (Trip) the journey of the ticket
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        stationFrom = trips.get(0).from();

        for (Trip trip : trips) {
            Preconditions.checkArgument(stationFrom.name().equals(trip.from().name()));
        }

        this.trips = new ArrayList<>(trips);
        Text = computeText();
    }


    /**
     * Constructor calling the previous Constructor
     *
     * @param from   (Station) Departure station
     * @param to     (Station) Arrival station
     * @param points (int) number of points given to the trip
     */
    public Ticket(Station from, Station to, int points) {
        this((List.of(new Trip(from, to, points))));

    }


    /**
     * Connectivity of the player holding the corresponding ticket
     *
     * @param connectivity (StationConnectivity) the connectivity given is that of the player holding the ticket
     * @return (int) returns the number of points the ticket is worth
     */
    public int points(StationConnectivity connectivity) {
        int point = Integer.MIN_VALUE;
        for (Trip trip : trips) {
            point = Integer.max(point, trip.points(connectivity));
        }
        return point;

    }

    /**
     * Returns the textual representation of the ticket
     *
     * @return (String) the textual representation of the ticket
     */
    public String text() {
        return Text;
    }


    /**
     * Compares the this and that banknotes in alphabetical order of their textual representation
     *
     * @param that (Ticket) the second ticket
     * @return (int) an integer depending on the lexicographic ordering between both ticket names
     */
    @Override
    public int compareTo(Ticket that) {
        // <0 this precedes that in the alphabet
        // >0 this is after that in the alphabet
        // 0  this and that are the same word
        return (this.text().compareTo(that.text()));
    }

    /**
     * Returns the textual representation of the ticket
     *
     * @return (String) the textual representation of the ticket
     */
    @Override
    public String toString() {
        return Text;
    }

    /**
     * Compute the text corresponding to the ticket
     *
     * @return (String) the text corresponding to the ticket
     */
    private String computeText() {
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> stationTo = new TreeSet<>();
        for (Trip trip : trips) {
            stationTo.add(String.format("%s (%s)", trip.to().name(), trip.points()));

        }
        return (stationTo.size() == 1) ?
                stringBuilder.append(stationFrom)
                        .append(" - ")
                        .append(String.join(", ", stationTo))
                        .toString()
                :
                stringBuilder.append(stationFrom)
                        .append(" - {")
                        .append(String.join(", ", stationTo))
                        .append("}")
                        .toString();
    }
}
