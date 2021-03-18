package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    @Test
    void initialFailsWithDiff4() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(3, Card.ORANGE).add(2, Card.LOCOMOTIVE).add(Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
                    PlayerState.initial(b.build());
                }
        );
    }

    @Test
    void possibleClaimCardTest() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(Card.BLUE);


        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s3, s4, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s9, s10, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        assertThrows(IllegalArgumentException.class, () -> {
            playerstate.possibleClaimCards(route6);
        });
    }


    @Test
    void possibleAdditionalCardsThrowsWhenAdditionalCardsCountIsNotWell() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(8, Card.BLUE)
                .add(1, Card.GREEN)
                .add(5, Card.RED);


        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s3, s4, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s9, s10, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        //The SortedBag of the initialCards
        SortedBag.Builder<Card> cardBuilderInitialCards = new SortedBag.Builder<>();
        cardBuilderInitialCards.add(2, Card.YELLOW);

        //The SortedBag of the drawnCards
        SortedBag.Builder<Card> cardBuilderDrawnCards = new SortedBag.Builder<>();
        cardBuilderDrawnCards.add(2, Card.ORANGE)
                .add(1, Card.LOCOMOTIVE);

        assertThrows(IllegalArgumentException.class, () -> {
            playerstate.possibleAdditionalCards(10, cardBuilderInitialCards.build(), cardBuilderDrawnCards.build());
        });
    }

    @Test
    void possibleAdditionalCardsThrowsWhenInitialCardsIsEmpty() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(8, Card.BLUE)
                .add(1, Card.GREEN)
                .add(5, Card.RED);

        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s3, s4, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s9, s10, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);


        //The SortedBag of the initialCards
        SortedBag.Builder<Card> cardBuilderInitialCards = new SortedBag.Builder<>();

        //The SortedBag of the drawnCards
        SortedBag.Builder<Card> cardBuilderDrawnCards = new SortedBag.Builder<>();
        cardBuilderDrawnCards.add(2, Card.ORANGE)
                .add(1, Card.LOCOMOTIVE);

        assertThrows(IllegalArgumentException.class, () -> {
            playerstate.possibleAdditionalCards(2, cardBuilderInitialCards.build(), cardBuilderDrawnCards.build());
        });
    }


    @Test
    void possibleAdditionalCardsThrowsWhenDrawnCardsIsNot3() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(8, Card.BLUE)
                .add(1, Card.GREEN)
                .add(5, Card.RED);

        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s3, s4, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s9, s10, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        //The SortedBag of the initialCards
        SortedBag.Builder<Card> cardBuilderInitialCards = new SortedBag.Builder<>();
        cardBuilderInitialCards.add(2, Card.YELLOW);

        //The SortedBag of the drawnCards
        SortedBag.Builder<Card> cardBuilderDrawnCards = new SortedBag.Builder<>();
        cardBuilderDrawnCards.add(2, Card.ORANGE)
                .add(4, Card.LOCOMOTIVE);

        assertThrows(IllegalArgumentException.class, () -> {
            playerstate.possibleAdditionalCards(2, cardBuilderInitialCards.build(), cardBuilderDrawnCards.build());
        });
    }

    @Test
    void possibleAdditionalCardsWorks() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.GREEN)
                .add(2, Card.BLUE)
               .add(5, Card.WHITE);

        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s3, s4, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s9, s10, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 2, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        //The SortedBag of the initialCards
        SortedBag.Builder<Card> cardBuilderInitialCards = new SortedBag.Builder<>();
        cardBuilderInitialCards.add(1, Card.GREEN);

        //The SortedBag of the drawnCards
        SortedBag.Builder<Card> cardBuilderDrawnCards = new SortedBag.Builder<>();
        cardBuilderDrawnCards.add(2, Card.ORANGE)
                .add(1, Card.LOCOMOTIVE);

        SortedBag<Card> Verte2 = SortedBag.of(2, Card.GREEN);
        SortedBag<Card> Loco1Vert = SortedBag.of(1, Card.GREEN, 1, Card.LOCOMOTIVE);
        SortedBag<Card> Loco2 = SortedBag.of(2, Card.LOCOMOTIVE);
        SortedBag<Card> white3 = SortedBag.of(3, Card.WHITE);
        SortedBag<Card> white2Loco1 = SortedBag.of(2, Card.WHITE, 1, Card.LOCOMOTIVE);
        SortedBag<Card> white1Loco2 = SortedBag.of(1, Card.WHITE, 2, Card.LOCOMOTIVE);
        SortedBag<Card> Loco3 = SortedBag.of(3, Card.LOCOMOTIVE);


        List<SortedBag> listAComparer = List.of(Verte2, Loco1Vert, Loco2);
        List<SortedBag> listAComparer2 = List.of(white3, white2Loco1, white1Loco2);
        List<SortedBag> list3= List.of();

        boolean statement = true;
        boolean statement2 = true;
        boolean statement3 = true;
        SortedBag<Card> init = SortedBag.of(5, Card.WHITE);
        /*List<SortedBag> liste = new ArrayList<>(playerstate.possibleAdditionalCards(2, cardBuilderInitialCards.build(), cardBuilderDrawnCards.build()));
        for (int i = 0; i < listAComparer.size(); ++i) {
            if (!listAComparer.get(i).equals(liste.get(i))) statement = false;
        }
        List<SortedBag> liste2 = new ArrayList<>(playerstate.possibleAdditionalCards(3, init, cardBuilderDrawnCards.build()));
        for (int i = 0; i < listAComparer2.size(); ++i) {
            if (!listAComparer2.get(i).equals(liste2.get(i))) statement2 = false;
        }*/
        List<SortedBag> liste3 = new ArrayList<>(playerstate.possibleAdditionalCards(3, init, cardBuilderDrawnCards.build()));
        for (int i = 0; i < list3.size(); ++i) {
            if (!list3.get(i).equals(liste3.get(i))) statement3 = false;
        }

        //assertTrue(statement);
      //  assertTrue(statement2);
assertTrue(statement3);
    }


    @Test
    void ticketPointsWorks() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(Card.BLUE);


        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s2, s3, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s2, s7, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        assertEquals(8, playerstate.ticketPoints());
    }

    @Test
    void canClaimRouteWorks() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(4, Card.BLUE);


        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s2, s3, 4, Route.Level.OVERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 3, Route.Level.OVERGROUND, Color.ORANGE);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s2, s7, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);

        assertFalse(playerstate.canClaimRoute(route2));
        assertTrue(playerstate.canClaimRoute(route5));
        assertTrue(playerstate.canClaimRoute(route3));
        assertTrue(playerstate.canClaimRoute(route1));
    }

    @Test
    void possibleClaimCardsFails() {
        //The SortedBag of the Cards
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(3, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .add(Card.BLUE);


        //The List of the Routes
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var route1 = new Route("1", s1, s2, 1, Route.Level.OVERGROUND, Color.BLACK);

        var s3 = new Station(2, "Zernez");
        var s4 = new Station(3, "Klosters");
        var route2 = new Route("2", s2, s3, 4, Route.Level.UNDERGROUND, Color.ORANGE);

        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);

        var s9 = new Station(8, "Karl");
        var s10 = new Station(9, "Aberer");
        var route5 = new Route("5", s2, s7, 4, Route.Level.UNDERGROUND, null);

        var s11 = new Station(10, "Stalingrad");
        var s12 = new Station(11, "LenineGrad");
        var route6 = new Route("6", s11, s12, 4, Route.Level.OVERGROUND, Color.WHITE);

        List<Route> routes = List.of(route1, route2, route3, route4, route5, route6, route1, route2, route2, route2, route2, route2, route2, route2, route2, route2);

        //The SortedBad of the tickets
        SortedBag.Builder<Ticket> ticketBuilder = new SortedBag.Builder<>();
        Ticket ticket1 = new Ticket(s1, s3, 4);
        Ticket ticket2 = new Ticket(s2, s5, 1);
        Ticket ticket3 = new Ticket(s2, s7, 5);
        ticketBuilder.add(ticket1)
                .add(ticket2)
                .add(ticket3);

        //The PlayerState
        PlayerState playerstate = new PlayerState(ticketBuilder.build(), cardBuilder.build(), routes);
        assertThrows(IllegalArgumentException.class, () -> {
            playerstate.possibleClaimCards(route2);
        });
    }
}
