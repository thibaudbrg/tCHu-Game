package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public final class Trail {
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;
    private int length = 0;

    private Trail(List<Route> routes) {
        this.routes = routes;
        for (Route route : routes) length = length + route.length();
        // the starting station (departure of the first road)
        station1 = routes.get(0).station1();
        // the arrival station (arrival of the last road)
        station2 = routes.get(routes.size()).station2();
    }


    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(null);
        } else {
            List<Trail> cs = getSingleTrailsPaths(routes);
            while (cs != null) {
                List<Trail> emptyTrailList = null;
                for (Trail trail : cs) {

                }
            }
        }
    }

    // Liste des chemins possibles composés d'une seule route

    //TODO : USINE A GAZ TROP COMPLIQUEE A REVOIR, TROP D'OBJETS CREES
    private static List<Trail> getSingleTrailsPaths(List<Route> routes) {
        ArrayList<Trail> littleTrails = new ArrayList<>();
        for (Route route : routes) {
            List<Route> r = new ArrayList<Route>();
            r.add(route);
            Trail t = new Trail(r);
            littleTrails.add(t);
        }
        return littleTrails;
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
