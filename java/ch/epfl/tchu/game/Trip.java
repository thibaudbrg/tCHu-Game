package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;

    }

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument((!(from.isEmpty() && to.isEmpty())) && points > 0);
        List<Trip> allList = new ArrayList<Trip>();
        for(Station stationFrom : from) {
            for (Station stationTo : to) {
                allList.add(new Trip(stationFrom, stationTo, points));
            }

        }
        return allList;
    }

    public Station from() {
        return from;
    }

    public Station to() {
        return to;
    }

    public int points() {
        return points;
    }

    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) {
            return points;
        } else return -points;

    }
}
