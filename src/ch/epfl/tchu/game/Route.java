package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Route
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Route {

    /**
     * Enum of Level
     */
    public enum Level {
        OVERGROUND, UNDERGROUND;
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Route constructor
     *
     * @param id       route id
     * @param station1 departure station
     * @param station2 arrival station
     * @param length   length of the Route
     * @param level    level of the Route
     * @param color    color of the Route
     * @throws NullPointerException
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) throws NullPointerException {
        Preconditions.checkArgument((!(station1.name().equals(station2.name()))) &&
                (length >= Constants.MIN_ROUTE_LENGTH) && (length <= Constants.MAX_ROUTE_LENGTH));
        if (id == null || station1 == null || station2 == null || level == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }

    /**
     * Return the id of the route
     *
     * @return the id of the course
     */
    public String id() {
        return id;
    }

    /**
     * Return the departure station of the route
     *
     * @return the departure station of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Return the arrival station of the route
     *
     * @return the arrival station of the route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Return the length of the Route
     *
     * @return the length of the Route
     */
    public int length() {
        return length;
    }

    /**
     * Return the level of the Route
     *
     * @return the level of the Route
     */
    public Level level() {
        return level;
    }

    /**
     * Return the color of the Route
     *
     * @return the color of the Route
     */
    public Color color() {
        return color;
    }

    /**
     * Return a list with the two stations in the order of the constructor
     *
     * @return a list with the two stations in the order of the constructor
     */
    public List<Station> stations() {
        List<Station> a = new ArrayList<Station>();
        a.add(station1);
        a.add(station2);
        return a;
    }

    /**
     * Return the station of the Route which is not given
     *
     * @param station Given station
     * @return the station of the Route which is not given
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument((station1.equals(station) ||
                station2.equals(station)));
        if (station1.name().equals(station.name())) {
            return station2;
        } else {
            return station1;
        }
    }

    /**
     * Return the number of points which is given when you claim the Route
     *
     * @return the number of points which is given when you claim the Route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Return a list of all possible combination of cards that allow you to claim the Route
     *
     * @return a list of all possible combination of cards that allow you to claim the Route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaim = new ArrayList();
        if (this.level == Level.UNDERGROUND) {
            if (color == null) {
                for (int i = 0; i < length; i++) {
                    for (Card card : Card.CARS) {
                        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
                        cardBuilder.add(length - i, card).add(i, Card.LOCOMOTIVE);
                        possibleClaim.add(cardBuilder.build());
                    }
                }
                SortedBag.Builder<Card> cardBuilder1 = new SortedBag.Builder<>();
                possibleClaim.add(cardBuilder1.add(length, Card.LOCOMOTIVE).build());
            } else for (int i = 0; i < length + 1; i++) {
                SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
                cardBuilder.add(length - i, Card.of(color)).add(i, Card.LOCOMOTIVE);
                possibleClaim.add(cardBuilder.build());
            }
        } else {
            if (color == null) {
                for (Card card : Card.CARS) {
                    SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
                    cardBuilder.add(length, card);
                    possibleClaim.add(cardBuilder.build());
                }
            } else {
                SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
                cardBuilder.add(length, Card.of(color));
                possibleClaim.add(cardBuilder.build());
            }
        }

        return possibleClaim;
    }

    /**
     * Return the number of card you have to play to claim a tunnel
     *
     * @param claimCards (SortedBag<Card>) List of claimcards (cards with which one tries to take over the tunnel)
     * @param drawnCards (SortedBag<Card>) List of drawncards (the three cards that the player has to pick)
     * @return (int) the number of card you have to play to claim a tunnel
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level == Level.UNDERGROUND && drawnCards.size() == 3);
        int i = 0;
        Card card = claimCards.get(0);

        for (Card card1 : drawnCards) {
            if (card1.color() == null || card1.color().equals(card.color())) {
                i++;
            }
        }
        return i;
    }
}