package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.Info.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoTest {

    @Test
    void worksWhenSaveralCardsNameAreAsked() {
        int count = 5;
        assertEquals("oranges", cardName(Card.ORANGE, count));
    }

    @Test
    void worksWhenACardNameIsAsked() {
        int count = 1;
        assertEquals("orange", cardName(Card.ORANGE, count));
    }

    @Test
    void drawMethodWorks() {
        assertEquals("\nAgathe et Hughes sont ex æqo avec 5 points !\n", draw(List.of("Agathe", "Hughes"), 5));
    }

    @Test
    void willPlayFirstMethodWorks() {
        Info i = new Info("Hughes");
        assertEquals("Hughes jouera en premier.\n\n", i.willPlayFirst());
    }

    @Test
    void keptTicketsMethodWorksWithNotPlural() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a gardé 1 billet.\n", i.keptTickets(1));
    }

    @Test
    void keptTicketsMethodWorksWithPlural() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a gardé 5 billets.\n", i.keptTickets(5));
    }

    @Test
    void canPlayMethodWorks() {
        Info i = new Info("Hughes");
        assertEquals("\nC'est à Hughes de jouer.\n", i.canPlay());
    }


    @Test
    void dewTicketsMethodWorksWithNotPlural() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a tiré 1 billet...\n", i.drewTickets(1));
    }


    @Test
    void dewTicketsMethodWorksWithPlural() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a tiré 5 billets...\n", i.drewTickets(5));
    }

    @Test
    void drewBlindCardMethodWorks() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a tiré une carte de la pioche.\n", i.drewBlindCard());
    }

    @Test
    void drewVisibleCardsMethodWorks() {
        Info i = new Info("Hughes");
        assertEquals("Hughes a tiré une carte LOCOMOTIVE visible.\n", i.drewVisibleCard(Card.LOCOMOTIVE));
    }

    @Test
    void claimedRouteCardsMethodWorks() {
        Info i = new Info("Hughes");
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"),
                2, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals("Hughes a pris possession de la route ALLEMAGNE – FRANCE au moyen de 1 verte et 1 orange.\n",
                i.claimedRoute(route, SortedBag.of(List.of(Card.ORANGE, Card.GREEN))));
    }

    @Test
    void attemptsTunnelClaimCardsWithLocomotiveMethodWorks() {
        Info i = new Info("Hughes");
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"),
                2, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals("Hughes tente de s'emparer du tunnel ALLEMAGNE – FRANCE au moyen de 1 locomotive et 1 orange !\n",
                i.attemptsTunnelClaim(route, SortedBag.of(List.of(Card.LOCOMOTIVE, Card.GREEN))));
    }

    @Test
    void attemptsTunnelClaimCardsWithNotLocomotiveMethodWorks() {
        Info i = new Info("Hughes");
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"),
                2, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals("Hughes tente de s'emparer du tunnel ALLEMAGNE – FRANCE au moyen de 1 verte et 1 orange !\n",
                i.attemptsTunnelClaim(route, SortedBag.of(List.of(Card.ORANGE, Card.GREEN))));
    }

    @Test
    void drewAdditionalCardsMethodWorksWithNotPlural() {
        Info i = new Info("Hughes");
        assertEquals("Les cartes supplémentaires sont {BLACK, GREEN, ORANGE}. Elles impliquent un coût additionnel de 1 carte.\n",
                i.drewAdditionalCards(SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.BLACK)), 1));
    }

    @Test
    void drewAdditionalCardsMethodWorksWithPlural() {
        Info i = new Info("Hughes");
        assertEquals("Les cartes supplémentaires sont {BLACK, GREEN, ORANGE}. Elles impliquent un coût additionnel de 15 cartes.\n",
                i.drewAdditionalCards(SortedBag.of(List.of(Card.ORANGE, Card.GREEN, Card.BLACK)), 15));
    }

    @Test
    void didNOtClaimRouteMethodWorks() {
        Info i = new Info("Hughes");
        Route route = new Route("sfd", new Station(2, "ALLEMAGNE"), new Station(3, "FRANCE"),
                2, Route.Level.OVERGROUND, Color.ORANGE);
        assertEquals("Hughes n'a pas pu (ou voulu) s'emparer de la route ALLEMAGNE – FRANCE.\n",
                i.didNotClaimRoute(route));
    }

    @Test
    void lastTurnBeginsMethodWorksWithNotPlural() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes n'a plus que 1 wagon, le dernier tour commence !\n",
                i.lastTurnBegins(1));
    }

    @Test
    void lastTurnBeginsMethodWorksWithPlural() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes n'a plus que 15 wagons, le dernier tour commence !\n",
                i.lastTurnBegins(15));
    }

    @Test
    void getsLongestTrailBonusMethodWorks() {
        Info i = new Info("Hughes");


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
        assertEquals("\nHughes reçoit un bonus de 10 points pour le plus long trajet" +
                " (Neuchâtel - Yverdon - Soleure - Berne - Lucerne - Fribourg (14)).\n", i.getsLongestTrailBonus(trail));
    }


    @Test
    void wonMethodWorksWithNotPlural() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes remporte la victoire avec 1 point, contre 1 point !\n", i.won(1, 1));
    }

    @Test
    void wonMethodWorksWithPlural1() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes remporte la victoire avec 1 point, contre 15 points !\n", i.won(1, 15));
    }

    @Test
    void wonMethodWorksWithPlural1bis() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes remporte la victoire avec 5 points, contre 1 point !\n", i.won(5, 1));
    }

    @Test
    void wonMethodWorksWithPlural2() {
        Info i = new Info("Hughes");
        assertEquals("\nHughes remporte la victoire avec 5 points, contre 15 points !\n", i.won(5, 15));
    }
}
