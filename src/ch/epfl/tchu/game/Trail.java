package ch.epfl.tchu.game;

import java.util.List;

public final class Trail {
    private final int length;
    private final Station station1;
    private final Station station2;


    private Trail(int length, Station station1, Station station2) {
        this.length = length;
        this.station1 = station1;
        this.station2 = station2;
    }

    // Constructeur de copie
    Trail(Trail t) {
        length = t.length;
        station1 = t.station1;
        station2 = t.station2;
    }

    /*

    CA CASSE LA TETE


    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(0, null, null);
        } else {
            int indexMax = 0;
            for(int i = 0; i < routes.size(); ++i) {
                if (routes.get(i).length() > routes.get(indexMax).length()) {
                    indexMax = i;
                }
            }
            return routes.get(indexMax);
        }
    }
*/

    public int length() {
        return length;
    }

    public Station station1() {
        if(this.length() == 0) return null;
        else return station1;
    }

    public Station station2() {
        if(this.length() == 0) return null;
        return station2;
    }


    @Override
    public String toString() {
        return station1 + " - " + String.join(" - ", ENSEMBLE DES STATIONS INTERMEDIAIRES) +
                " - " +  station2 + " (" + length + ") ";
    }
}
