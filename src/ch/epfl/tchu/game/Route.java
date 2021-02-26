package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

public final class Route {
    public enum Level {
        OVERGROUND, UNDERGROUND;
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

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

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }

    public List<Station> stations() {
        List<Station> a = new ArrayList<Station>();
        a.add(station1);
        a.add(station2);
        return a;


    }

    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station1.name().equals(station.name()) ||
                station2.name().equals(station.name()));
        if (station1.name().equals(station.name())) {
            return station2;
        } else {
            return station1;
        }
    }

    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaim = new ArrayList();
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

        return possibleClaim; // TODO COMPLETE
    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level==Level.UNDERGROUND && drawnCards.size()==3);

        return 0; // TODO COMPLETE
    }

}