package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class PublicPlayerState {
    private int ticketCount;
    private int cardCount;
    private List<Route> routes;
    private int carCount ;
    private int claimPoints;


    PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0);
        Preconditions.checkArgument(cardCount >= 0);
        List<Route> routesCopy = new ArrayList<>(routes);
        routes = routesCopy;
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        carCount = Constants.INITIAL_CAR_COUNT -scanRouteList(false);
        claimPoints = scanRouteList(true);

    }

    public int ticketCount() {
        return ticketCount;
    }

    public int cardCount() {
        return cardCount;
    }

    public List<Route> routes() {
        return routes;
    }

    public int carCount() {
        return carCount;
    }

    public int claimPoints() {
        return claimPoints;
    }

    private int scanRouteList(boolean type) {
        int points = 0;
        int length = 0;
        for (Route road : routes) {
            points += road.claimPoints();
            length += road.length();
        }
        return (type ? points : length);
    }
}