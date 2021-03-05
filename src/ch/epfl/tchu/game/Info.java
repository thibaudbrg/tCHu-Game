package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.StringsFr;

import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.plural;

public final class Info {
    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }

    public static String cardName(Card card, int count) {
        switch (card.color()) {
            case BLACK:
                return StringsFr.BLACK_CARD + plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + plural(count);
            case RED:
                return StringsFr.RED_CARD + plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + plural(count);
            default:
                return StringsFr.LOCOMOTIVE_CARD + plural(count);

        }
    }

    private static String routeName(Route route) {
        return route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
    }

    private static String cardDescription(SortedBag<Card> cards) {
        StringBuilder result = new StringBuilder();
        int index = 1;
        for (Card c : cards.toSet()) {
            if (index < cards.toSet().size() - 1) {
                result.append(cards.countOf(c)).append(" ").append(cardName(c, cards.countOf(c))).append(", ");
            } else if (index == cards.toSet().size() - 1) {
                result.append(cards.countOf(c)).append(" ").append(cardName(c, cards.countOf(c))).append("et ");
            } else {
                result.append(cards.countOf(c)).append(" ").append(cardName(c, cards.countOf(c)));
            }
            index++;

        }
        return result.toString();
    }

    public static String draw(List<String> playerNames, int points) {
        return String.format(StringsFr.DRAW, playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1), points);
    }

    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count);
    }

    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count);
    }

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, card);
    }

    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeName(route), cardDescription(cards));
    }

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardDescription(initialCards));
    }

    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        if (additionalCost == 0) {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, drawnCards)
                    + String.format(StringsFr.NO_ADDITIONAL_COST);
        } else
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, drawnCards)
                    + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost));
    }

    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    public String getLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail);
    }

    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, plural(points), loserPoints, plural(loserPoints));
    }
}
