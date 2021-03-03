package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrailTest {


    /**
     * void ConstructorWorksOnLengthCalculus() {
     * Station station1 = new Station(2, "Yverdon");
     * Station station2 = new Station(3, "Délémont");
     * List<Route> route = new ArrayList<>();
     * int length = 6;
     * Trail trail = new Trail(route, station1, station2);
     * assertEquals(length, trail.length());
     * }
     *
     * @Test void CSHas12Trails() {
     * int number = 0;
     * List<Route> route = new ArrayList<>();
     * assertEquals(12, number);
     * }
     */

    @Test
    void longest() {
        Station Neuchatel = new Station(1, "Neuchâtel");
        Station Yverdon = new Station(2, "Yverdon");
        Station Soleure = new Station(3, "Soleure");
        Station Berne = new Station(4, "Berne");
        Station Lucerne = new Station(5, "Lucerne");
        Station Fribourg = new Station(6, "Fribourg");

        Route a = new Route("A", Neuchatel, Yverdon, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route b = new Route("B", Neuchatel, Soleure, 4, Route.Level.OVERGROUND, Color.BLACK);
        Route c = new Route("C", Neuchatel, Berne, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route d = new Route("D", Berne, Soleure, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route e = new Route("E", Berne, Lucerne, 4, Route.Level.OVERGROUND, Color.BLACK);
        Route f = new Route("F", Berne, Fribourg, 1, Route.Level.OVERGROUND, Color.BLACK);

        List<Route> route = new ArrayList<>();
        route.add(a);
        route.add(b);
        route.add(c);
        route.add(d);
        route.add(e);
        route.add(f);
        assertEquals("BJR", Trail.longest(route).toString());
    }

    @Test
    void WorksWhenToStringIsCalling() {
        Station Neuchatel = new Station(1, "Neuchâtel");
        Station Yverdon = new Station(2, "Yverdon");
        Station Soleure = new Station(3, "Soleure");
        Station Berne = new Station(4, "Berne");
        Station Lucerne = new Station(5, "Lucerne");
        Station Fribourg = new Station(6, "Fribourg");

        Route a = new Route("A", Neuchatel, Yverdon, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route b = new Route("B", Soleure, Yverdon, 4, Route.Level.OVERGROUND, Color.BLACK);
        Route c = new Route("C", Berne, Soleure, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route d = new Route("D", Berne, Lucerne, 2, Route.Level.OVERGROUND, Color.BLACK);
        Route e = new Route("E", Lucerne, Fribourg, 4, Route.Level.OVERGROUND, Color.BLACK);

        List<Route> route = new ArrayList<>();
        route.add(a);
        route.add(b);
        route.add(c);
        route.add(d);
        route.add(e);

        Trail trail = Trail.newTrailForTests(route, Neuchatel, Fribourg);

        String s = "Neuchâtel - Yverdon - Soleure - Berne - Lucerne - Fribourg (14)";
        assertEquals(s, (trail.toString()));

    }
}