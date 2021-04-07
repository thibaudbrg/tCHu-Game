package ch.epfl.tchu.game;

/**
 * Represents the connectivity of a player station network
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public interface StationConnectivity {

    /**
     * Return if two stations are connected with card to each other
     *
     * @param s1 (Station) Station 1
     * @param s2 (Station) Station 2
     * @return (boolean) if the 2 station are connected
     */
    boolean connected(Station s1, Station s2);
}
