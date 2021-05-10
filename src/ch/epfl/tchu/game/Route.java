package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
        OVERGROUND, UNDERGROUND
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
     * @param id       (String) route id
     * @param station1 (Station) first station
     * @param station2 (Station) second station
     * @param length   (int) length of the Route
     * @param level    (Level) level of the Route
     * @param color    (Color) color of the Route
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.id()== (station2.id())));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH);
        Preconditions.checkArgument(length <= Constants.MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }


    /**
     * Return a list with the two stations in the order of the constructor
     *
     * @return (List<Station>) a list with the two stations in the order of the constructor
     */
    public List<Station> stations() {
        List<Station> stationList = new ArrayList<>();
        stationList.add(station1);
        stationList.add(station2);

        return stationList;
    }

    /**
     * Return the station of the Route which is not given
     *
     * @param station (Station) Given station
     * @return (Station) the station of the Route which is not given
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument((station1.equals(station) || station2.equals(station)));

        return station1.name().equals(station.name()) ? station2 : station1;
    }

    /**
     * Return the number of points which is given when you claim the Route
     *
     * @return (int) the number of points which is given when you claim the Route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Return a list of all possible combination of cards that allow you to claim the Route
     *
     * @return (List<SortedBag<Card>>) a list of all possible combination of cards that allow you to claim the Route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaim = new LinkedList<>();

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
            } else
                for (int i = 0; i < length + 1; i++) {
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
        Preconditions.checkArgument(level == Level.UNDERGROUND);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        int additionalCardsCount = 0;
        Card card = claimCards.get(0);

        for (Card card1 : drawnCards) {
            if (card1.color() == null || card1.color().equals(card.color())) {
                ++additionalCardsCount;
            }
        }
        return additionalCardsCount;
    }


    /**
     * Return the id of the route
     *
     * @return (String) the id of the course
     */
    public String id() {
        return id;
    }

    /**
     * Return the departure station of the route
     *
     * @return (Station) the departure station of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Return the arrival station of the route
     *
     * @return (Station) the arrival station of the route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Return the length of the Route
     *
     * @return (int) the length of the Route
     */
    public int length() {
        return length;
    }

    /**
     * Return the level of the Route
     *
     * @return (Level) the level of the Route
     */
    public Level level() {
        return level;
    }

    /**
     * Return the color of the Route
     *
     * @return (Color) the color of the Route
     */
    public Color color() {
        return color;
    }

}