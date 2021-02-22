package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public class Station {
    private int id;
    private String name;

    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
