package ch.epfl.tchu.game;

import java.util.*;

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
    private int length = 0;

    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
        if (!routes.isEmpty()) {
            for (Route route : routes) {
                length += route.length();
            }
        }
    }

    /**
     * Determines the longest Trail of the network made up of the given Routes
     *
     * @param routes (Route) All Routes that belong to the player
     * @return One of the longest Trail
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(new ArrayList<Route>(), null, null);
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

                    for (Route playerRoad : routes) {
                        if (!c.routes.contains(playerRoad) && !c.routes.contains(new Route(playerRoad.id(), playerRoad.station2(), playerRoad.station1(), playerRoad.length(), playerRoad.level(), playerRoad.color())) && playerRoad.station1().id() == c.station2.id()) {
                            rs.add(playerRoad);
                        }

                    }
                    if (!rs.isEmpty()) {
                        for (Route r : rs) {
                            List<Route> road =new ArrayList<>(c.routes);
                            road.add(r);
                            Trail t = new Trail(road, c.station1, r.station2());
                            emptyCS.add(t);
                        }

                    }

                } cs = emptyCS;

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
            Trail t1 = new Trail(singleRoute, route.station1(), route.station2());
            Trail t2 = new Trail(singleRoute, route.station2(), route.station1());
            cs.add(t1);
            cs.add(t2);
        }
        return cs;
    }

    /**
     * Calculates all possible Trails and lists them in a List
     *
     * @param routes (Route) All Routes that belong to the player
     * @return (List < Trail >) List of all possible Trails
     */
    private static List<Trail> getAllTrails(List<Route> routes) {
        List<Trail> cs = findCS(routes);
        List<Trail> allTrail = findCS((routes));
        while (!cs.isEmpty()) {
            ArrayList<Trail> emptyCS = null;
            for (Trail c : cs) {
                List<Route> rs = null;

                for (Route playerRoad : routes) {
                    if (!c.routes.contains(playerRoad) && playerRoad.station1().id() == c.station2.id()) {
                        rs.add(playerRoad);
                    }

                }
                for (Route r : rs) {
                    c.routes.add(r);
                    Trail t = new Trail(c.routes, c.station1, r.station2());
                    emptyCS.add(t);
                    allTrail.add(t);
                }

            }
            cs = emptyCS;
        }
        return allTrail;
    }

    /**
     * Lists all the stations of a trail
     *
     * @param trail (Trail) The Trail concerned
     * @return (List < String >) A list of all the stations of the Trail
     */
    public List<String> listStation(Trail trail) {
        ArrayList<String> listStation = new ArrayList();
        for (Route route : trail.routes) {
            listStation.add(route.station1().toString());
        }
        listStation.add(trail.station2().toString());
        return listStation;
    }

    /**
     * Method that instantiates any Trail for unit tests --Not Definite--.
     *
     * @param routes   (List<Route>) the List of the Trail
     * @param station1 (Station) the depart Station of the Trail
     * @param station2 (Station) The arrival Station of the Trail
     * @return the Trail
     */
    public static Trail newTrailForTests(List<Route> routes, Station station1, Station station2) {
        return new Trail(routes, station1, station2);
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
        if (this.length() == 0) return null;
        else return station1;
    }

    /**
     * Returns the arrival Station of the Trail of null if there is any Station
     *
     * @return the arrival Station of the Trail of null if there is any Station
     */
    public Station station2() {
        if (this.length() == 0) return null;
        return station2;
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
