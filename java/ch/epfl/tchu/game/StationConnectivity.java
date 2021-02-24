package ch.epfl.tchu.game;

public interface StationConnectivity {

    /**
     * Return if two stations are connected with card to each other
     *
     * @param s1 (Station) Station 1
     * @param s2 (Station) Station 2
     * @return
     */
    public abstract boolean connected(Station s1, Station s2);
}
