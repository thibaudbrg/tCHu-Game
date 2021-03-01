package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouteTest {

    @Test
    void construtorFailsWithEqualsStation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("sfd", new Station(2, "FRANCE"), new Station(3, "FRANCE"), 3, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void construtorFailsWithLengthOutOfBorder() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 7, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void construtorFailsWithNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Route(null, new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 5, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void construtorFailsWithNullStation() {
        assertThrows(NullPointerException.class, () -> {
            new Route("sfd", null, new Station(2, "FRANCE"), 5, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void construtorFailsWithNullStation2() {
        assertThrows(NullPointerException.class, () -> {
            new Route("sfd", new Station(2, "ALLEMAGNE"), null, 5, Route.Level.OVERGROUND, Color.BLUE);
        });
    }

    @Test
    void construtorFailsWithNullLevel() {
        assertThrows(NullPointerException.class, () -> {
            new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 5, null, Color.BLUE);
        });
    }

    @Test
    void testStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(2, "ALLEMAGNE"));
        stations.add(new Station(3, "FRANCE"));

        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 5, Route.Level.OVERGROUND, Color.BLUE);
        assertEquals(stations, route.stations());
    }

    @Test
    void stationOppositeFailsWithUnknownStation() {
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 5, Route.Level.OVERGROUND, Color.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            route.stationOpposite(new Station(1, "BERNE"));
        });
    }

    @Test
    void testStationOpposite() {
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 5, Route.Level.OVERGROUND, Color.BLUE);
        assertEquals(new Station(2, "ALLEMAGNE").name(), route.stationOpposite(route.station2()).name());
    }

    @Test
    void testPossibleClaimWithNullColorRoad() {
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.OVERGROUND, null);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag<Card> e1 = cardBuilder.add(2, Card.BLACK).build();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag<Card> e2 = cardBuilder2.add(2, Card.VIOLET).build();
        SortedBag.Builder<Card> cardBuilder3 = new SortedBag.Builder<>();
        SortedBag<Card> e3 = cardBuilder3.add(2, Card.BLUE).build();
        SortedBag.Builder<Card> cardBuilder4 = new SortedBag.Builder<>();
        SortedBag<Card> e4 = cardBuilder4.add(2, Card.GREEN).build();
        SortedBag.Builder<Card> cardBuilder5 = new SortedBag.Builder<>();
        SortedBag<Card> e5 = cardBuilder5.add(2, Card.YELLOW).build();
        SortedBag.Builder<Card> cardBuilder6 = new SortedBag.Builder<>();
        SortedBag<Card> e6 = cardBuilder6.add(2, Card.ORANGE).build();
        SortedBag.Builder<Card> cardBuilder7 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder8 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder9 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder10 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder11 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder12 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder13 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder14 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder15 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder16 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder17 = new SortedBag.Builder<>();
        SortedBag<Card> e7 = cardBuilder7.add(2, Card.RED).build();
        SortedBag<Card> e8 = cardBuilder8.add(2, Card.WHITE).build();
        SortedBag<Card> e9 = cardBuilder9.add(Card.BLACK).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e10 = cardBuilder10.add(Card.VIOLET).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e11 = cardBuilder11.add(Card.BLUE).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e12 = cardBuilder12.add(Card.GREEN).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e13 = cardBuilder13.add(Card.YELLOW).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e14 = cardBuilder14.add(Card.ORANGE).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e15 = cardBuilder15.add(Card.RED).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e16 = cardBuilder16.add(Card.WHITE).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e17 = cardBuilder17.add(2, Card.LOCOMOTIVE).build();

        List<SortedBag<Card>> f = new ArrayList<SortedBag<Card>>();
        f.add(e1);
        f.add(e2);
        f.add(e3);
        f.add(e4);
        f.add(e5);
        f.add(e6);
        f.add(e7);
        f.add(e8);
        f.add(e9);
        f.add(e10);
        f.add(e11);
        f.add(e12);
        f.add(e13);
        f.add(e14);
        f.add(e15);
        f.add(e16);
        f.add(e17);
        int z = 0;
        if (f.size() == route.possibleClaimCards().size()) {
            for (int i = 0; i < f.size(); i++) {
                if (f.get(i).equals(route.possibleClaimCards().get(i))) {
                    z++;
                }
            }
        }
        assertEquals(f.size(), z);
    }

    @Test
    void testPossibleClaimWithColoredCard() {
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.OVERGROUND, Color.ORANGE);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder3 = new SortedBag.Builder<>();
        List<SortedBag<Card>> f = new ArrayList<SortedBag<Card>>();
        SortedBag<Card> e1 = cardBuilder.add(2, Card.ORANGE).build();
        SortedBag<Card> e2 = cardBuilder2.add(Card.ORANGE).add(Card.LOCOMOTIVE).build();
        SortedBag<Card> e3 = cardBuilder3.add(2, Card.LOCOMOTIVE).build();
        int z = 0;
        if (f.size() == route.possibleClaimCards().size()) {
            for (int i = 0; i < f.size(); i++) {
                if (f.get(i).equals(route.possibleClaimCards().get(i))) {
                    z++;
                }
            }

        }
        assertEquals(f.size(), z);
    }

    @Test
    void additionalClaimCardsCountFailsWithoutTunnel(){
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.OVERGROUND, Color.ORANGE);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag<Card> e1=cardBuilder.add(2,Card.ORANGE).build();
        SortedBag<Card> e2=cardBuilder2.add(Card.LOCOMOTIVE).add(Card.ORANGE).add(Card.GREEN).build();

        assertThrows(IllegalArgumentException.class,()->{route.additionalClaimCardsCount(e1,e2);});
    }
    @Test
    void additionalClaimCardsCountFailsWithNot3Card(){
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag<Card> e1=cardBuilder.add(2,Card.ORANGE).build();
        SortedBag<Card> e2=cardBuilder2.add(Card.LOCOMOTIVE).build();

        assertThrows(IllegalArgumentException.class,()->{route.additionalClaimCardsCount(e1,e2);});
    }
    @Test
    void additionalClaimCardsCountFailsWithNot3Card2(){
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag<Card> e1=cardBuilder.add(2,Card.ORANGE).build();
        SortedBag<Card> e2=cardBuilder2.add(10,Card.LOCOMOTIVE).build();

        assertThrows(IllegalArgumentException.class,()->{route.additionalClaimCardsCount(e1,e2);});
    }
    @Test
    void additionalClaimCardsCountWorks(){
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 2, Route.Level.UNDERGROUND, Color.ORANGE);
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder2 = new SortedBag.Builder<>();
        SortedBag<Card> e1=cardBuilder.add(2,Card.ORANGE).build();
        SortedBag<Card> e2=cardBuilder2.add(Card.LOCOMOTIVE).add(Card.ORANGE).add(Card.GREEN).build();

        assertEquals(2,route.additionalClaimCardsCount(e1,e2));
    }

   @Test

   void claimPointTest(){
       Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"), 3, Route.Level.UNDERGROUND, Color.ORANGE);
       assertEquals(4,route.claimPoints());
   }
}

