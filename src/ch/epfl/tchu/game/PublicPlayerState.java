package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the public part of a player's state, i.e. the number of tickets, cards and wagons he owns,
 * the roads he has taken, and the number of building points he has obtained
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final int carCount;
    private final int claimPoints;
    private final List<Route> routes;

    /**
     * Constructs the public state of a player who has the given number of tickets and cards,
     * and has seized the given routes
     *
     * @param ticketCount (int) The number of tickets the player owns
     * @param cardCount   (int) The number of cards the player owns
     * @param routes      (List<Route>) The roads that the player has taken over
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0);
        Preconditions.checkArgument(cardCount >= 0);
        this.routes = new ArrayList<>(routes);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        carCount = Constants.INITIAL_CAR_COUNT - routeListLength();
        claimPoints = routeListPoints();

    }

    /**
     * Returns the number of tickets of the player
     *
     * @return the number of tickets of the player
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Returns the number of cards of the player
     *
     * @return the number of cards of the player
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Returns a list of all the roads claimed by the player
     *
     * @return a list of all the roads claimed by the player
     */
    public List<Route> routes() {
        return new ArrayList<>(routes);
    }

    /**
     * Returns the number of cars owned by the player
     *
     * @return the number of cars owned by the player
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Returns the number of points claimed by the player
     *
     * @return the number of points claimed by the player
     */
    public int claimPoints() {
        return claimPoints;
    }

    private int routeListPoints() {
        int points = 0;
        for (Route road : routes) {
            points += road.claimPoints();
        }
        return points;
    }

    private int routeListLength() {
        int length = 0;
        for (Route road : routes) {
            length += road.length();
        }
        return length;
    }
}