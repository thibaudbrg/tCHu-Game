package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * La description de la classe
 *
 * @author Decotignie Matthieu
 * @author Bourgeois Thibaud (324604)
 */
public final class Ticket implements Comparable<Ticket> {
    private final String finalText;
    private List<Trip> trips;
    private final Station stationForm;

    /**
     * La description du constructeur
     *
     * @param trips
     */
    Ticket(List<Trip> trips) {
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
     * La description du cosntructeur
     *
     * @param from
     * @param to
     * @param points
     */
    Ticket(Station from, Station to, int points) {
        this((List.of(new Trip(from, to, points))));

    }

    public String text() {
        return finalText;
    }

    /**
     * La description de la méthode
     *
     * @return
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


    int points(StationConnectivity connectivity) {

        return 0; //TODO COMPLETE
    }


    /**
     *
     *
     * @param that
     * @return
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
