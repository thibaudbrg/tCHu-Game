package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

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

    Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
        for (Route route : routes) {
            length += route.length();
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
            return new Trail(null, null, null);
        } else {
            List<Trail> allTrails = getAllTrails(routes);
            int index = 0;
            int length = 0;
            for (int i = 0; i < allTrails.size(); ++i) {
                if (allTrails.get(i).length() > length) {
                    index = i;
                    length = allTrails.get(i).length();
                }
            }
            return allTrails.get(index);
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
        while (cs != null) {
            ArrayList<Trail> emptyCS = null;
            ArrayList<Route> rs = new ArrayList<>();
            for (Trail c : cs) {
                for (Route route : routes) {
                    if (c.routes.contains(route) == false) {
                        rs.add(route);
                    }
                }
                for (Route r : rs) {
                    c.routes.add(r);
                    emptyCS.add(c);
                }
            }
            cs = emptyCS;
        }
        return cs;
    }

    public int length() {
        return length;
    }

    public Station station1() {
        if (this.length() == 0) return null;
        else return station1;
    }


    public Station station2() {
        if (this.length() == 0) return null;
        return station2;
    }


    @Override
    public String toString() {
        ArrayList<String> listStation = new ArrayList();
        for (Route route : routes) {
            listStation.add(route.station1().toString());
            listStation.add(route.station2().toString());
        }
        return String.join(" - ", listStation) + " " + (this.length());
    }
}
