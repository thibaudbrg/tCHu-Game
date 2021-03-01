package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrailTest {

    @Test
    void ConstructorWorksOnLengthCalculus() {
        Station station1 = new Station(2, "Yverdon");
        Station station2 = new Station(3, "Délémont");
        List<Route> route = new ArrayList<>();
        int length = 6;
        Trail trail = new Trail(route, station1, station2);
        assertEquals(length, trail.length());
    }

    @Test
    void CSHas12Trails() {
        int number = 0;
        List<Route> route = new ArrayList<>();
        assertEquals(12, number);
    }
