package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * Represents a ticket
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Ticket implements Comparable<Ticket> {
    private final String finalText;
    private List<Trip> trips;
    private final Station stationForm;

    /**
     * Built a ticket
     *
     * @param trips (Trip) the journey of the ticket
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());

        for (Trip trip : trips) {
            for (Trip trip1 : trips) {
                Preconditions.checkArgument(trip.from().name().equals(trip1.from().name()));
            }
        }
        this.trips = trips;
        stationForm = trips.get(0).from();
        finalText = computeText();
    }


    /**
     * Constructor calling the previous Constructor
     *
     * @param from (Station) Departure station
     * @param to (Station) Arrival station
     * @param points (int) number of points given to the trip
     */
    public Ticket(Station from, Station to, int points) {
        this((List.of(new Trip(from, to, points))));

    }

    public String text() {
        return finalText;
    }


    /**
     * Compute the text corresponding to the ticket
     *
     * @return (String) the text corresponding to the ticket
     */
    private String computeText() {
        TreeSet<String> stationTo = new TreeSet<>();
        for (Trip trip : trips) {
            stationTo.add(String.format("%s (%s)", trip.to().name(), trip.points()));

        }
        if (stationTo.size() == 1) {
            return stationForm + " - " + String.join(", ", stationTo);
        } else {
            return stationForm + " - " + "{" + String.join(", ", stationTo) + "}";
        }

    }

    /**
     * Connectivity of the player holding the corresponding ticket
     *
     * @param connectivity (StationConnectivity) the connectivity given is that of the player holding the ticket
     * @return (int) returns the number of points the ticket is worth
     */
    public int points(StationConnectivity connectivity) {
        int maxPoint = 0;
        int minPoint = Integer.MAX_VALUE;
        for (Trip trip : trips) {
            if (connectivity.connected(stationForm, trip.to())) {
                maxPoint = Integer.max(maxPoint,trip.points());
            }
           minPoint= Integer.min(minPoint,trip.points());
        }
        if (maxPoint == 0) {
            return -minPoint;
        } else return maxPoint;
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

    @Override
    public String toString() {
        return finalText;
    }
}
