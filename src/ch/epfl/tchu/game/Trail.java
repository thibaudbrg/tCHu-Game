package ch.epfl.tchu.game;

import java.util.List;
import java.util.ArrayList;

/**
 * Class that represents a trail in a player's network.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Trail {
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;
    private final int length;

    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = new ArrayList<>(routes);
        this.station1 = station1;
        this.station2 = station2;

        int lengthConstructor = 0 ;
        if (!routes.isEmpty()) {
            for (Route route : routes) {
                lengthConstructor += route.length();
            }
        }
        length = lengthConstructor;
    }

    /**
     * Determines the longest Trail of the network made up of the given Routes
     *
     * @param routes (Route) All Routes that belong to the player
     * @return One of the longest Trail
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(new ArrayList<>(), null, null);
        } else {
            List<Trail> cs = findCS(routes);
            Trail longestTrail = cs.get(0);
            while (!cs.isEmpty()) {
                ArrayList<Trail> emptyCS = new ArrayList<>();
                for (Trail c : cs) {
                    if (c.length > longestTrail.length) {
                        longestTrail = c;
                    }
                    List<Route> rs = new ArrayList<>();
                    for (Route playerRoute : routes) {
                        if (!c.routes.contains(playerRoute) && (playerRoute.station1().id() == c.station2.id()
                                || playerRoute.station2().id() == c.station2.id())) {
                            rs.add(playerRoute);
                        }
                    }
                    if (!rs.isEmpty()) {
                        for (Route r : rs) {
                            List<Route> road = new ArrayList<>(c.routes);
                            road.add(r);
                            Trail t = new Trail(road, c.station1, r.stationOpposite(c.station2));
                            emptyCS.add(t);
                        }
                    }
                }
                cs = emptyCS;
            }
            return longestTrail;
        }
    }


    /**
     * List of possible Trails consisting of a single Route
     *
     * @param routes (Route) All Routes that belong to the player
     * @return (List < Trail >) List of all possible Trails consisting of a single Route
     */
    private static List<Trail> findCS(List<Route> routes) {
        ArrayList<Trail> cs = new ArrayList<>();
        for (Route route : routes) {
            ArrayList<Route> singleRoute = new ArrayList<>();
            singleRoute.add(route);
            for(Station station : route.stations()){
                cs.add(new Trail(singleRoute,station, route.stationOpposite(station)));
            }
        }
        return cs;
    }

    /**
     * Lists all the stations of a trail
     *
     * @param trail (Trail) The Trail concerned
     * @return (List < String >) A list of all the stations of the Trail
     */
    public List<String> listStation(Trail trail) {
        ArrayList<String> listStation = new ArrayList();

        Station opposite = trail.station1;
        for (Route route : trail.routes) {
            listStation.add(opposite.toString());
            opposite = route.stationOpposite(opposite);
        }
        listStation.add(trail.station2().toString());

        return listStation;
    }

    /**
     * Returns the length of the Trail
     *
     * @return the length of the Trail
     */
    public int length() {
        return length;
    }

    /**
     * Returns the depart Station of the Trail of null if there is any Station
     *
     * @return the depart Station of the Trail of null if there is any Station
     */
    public Station station1() {
        return this.length() == 0 ? null :station1;
    }

    /**
     * Returns the arrival Station of the Trail of null if there is any Station
     *
     * @return the arrival Station of the Trail of null if there is any Station
     */
    public Station station2() {
        return this.length() == 0 ? null :station2;
    }

    /**
     * Returns the list provided by all Trail stations followed by the length of the Trail.
     *
     * @return the list provided by all Trail stations followed by the length of the Trail.
     */
    @Override
    public String toString() {
        List<String> listStation = listStation(this);
        return String.join(" - ", listStation) + " " + "(" + (this.length() + ")");
    }
}