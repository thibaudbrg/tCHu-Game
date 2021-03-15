package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class publicPlayerStateTest {

    @Test
    void constructorFailsWithNegativeStuff() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-1, 2, List.of());
        });
    }

    @Test
    void constructorFailsWithNegativeStuff1() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(2, -1, List.of());
        });
    }

    @Test
    void ticketCountWork() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        PublicPlayerState p = new PublicPlayerState(2, 3, List.of(new Route("1", s1, s2, 5, Route.Level.OVERGROUND, Color.ORANGE)));
        assertEquals(2, p.ticketCount());
    }

    @Test
    void cardCountWork() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        PublicPlayerState p = new PublicPlayerState(2, 3, List.of(new Route("1", s1, s2, 5, Route.Level.OVERGROUND, Color.ORANGE)));
        assertEquals(3, p.cardCount());

    }

    @Test
    void routeF() {

        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        Route r1 = new Route("1", s1, s2, 5, Route.Level.OVERGROUND, Color.ORANGE);
        Route r2 = new Route("2", s1, s2, 3, Route.Level.OVERGROUND, Color.ORANGE);
        PublicPlayerState p = new PublicPlayerState(2, 3, List.of(r1, r2));
        assertEquals(32, p.carCount());
    }

    @Test
    void claimP() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        Route r1 = new Route("1", s1, s2, 5, Route.Level.OVERGROUND, Color.ORANGE);
        Route r2 = new Route("2", s1, s2, 3, Route.Level.OVERGROUND, Color.ORANGE);
        PublicPlayerState p = new PublicPlayerState(2, 3, List.of(r1, r2));
        assertEquals(14, p.claimPoints());
    }
}

