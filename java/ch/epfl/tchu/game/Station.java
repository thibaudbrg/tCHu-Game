package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * La description de la classe
 *
 * @author Decotignie Matthieu
 * @author Bourgeois Thibaud (324604)
 */
public class Station {
    private final int id;
    private final String name;

    /**
     * La description du constructeur
     *
     * @param id
     * @param name
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }


    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
