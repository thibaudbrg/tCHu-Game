package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.EN_DASH_SEPARATOR;
import static ch.epfl.tchu.gui.StringsFr.plural;

/**
 * Generate most of the in-game messages
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Info {
    private final String playerName;

    /**
     * Public constructor
     *
     * @param playerName name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Returns a strings with the card's name
     *
     * @param card  (Card) given card
     * @param count (int) indicates if we use the plural or not
     * @return (String) a strings with the card's name
     */
    public static String cardName(Card card, int count) {
        if (card.color() == null)
            return StringsFr.LOCOMOTIVE_CARD + plural(count);
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
                throw  new Error();

        }
    }

    /**
     * Returns a string, declaring that the game finished and that the players are ex aeqo with (points) points.
     *
     * @param playerNames (List<String>) list of the player's names
     * @param points      (int) number of points of the players
     * @return (String) a String declaring that the game finished and that the players are ex aeqo with (points) points
     */
    public static String draw(List<String> playerNames, int points) {
        return String.format(StringsFr.DRAW, playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1), points);
    }

    private static String routeName(Route route) {
        return route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
    }

    /**
     * Created the description of each of the cards and their numbers contained in a SortedBag
     *
     * @param cards (SortedBag<Card>) The cards
     * @return (String) The description
     */
    public static String cardDescription(SortedBag<Card> cards) {
        StringBuilder result = new StringBuilder();
        int index = 1;
        for (Card c : cards.toSet()) {
            if (index < cards.toSet().size() - 1) {
                result.append(cards.countOf(c))
                        .append(" ")
                        .append(cardName(c, cards.countOf(c)))
                        .append(", ");
            } else if (index == cards.toSet().size() - 1) {
                result.append(cards.countOf(c))
                        .append(" ")
                        .append(cardName(c, cards.countOf(c)))
                        .append(StringsFr.AND_SEPARATOR);
            } else {
                result.append(cards.countOf(c))
                        .append(" ")
                        .append(cardName(c, cards.countOf(c)));
            }
            index++;
        }
        return result.toString();
    }




    /**
     * Returns a String declaring which player plays first
     *
     * @return (String) a String declaring which player plays first
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Returns a String declaring that the player kept count ticket(s)
     *
     * @param count (int) number of ticket kept
     * @return (String) a String declaring that the player kept count ticket(s)
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a String declaring that the player can play
     *
     * @return (String) a String declaring that the player can play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Returns a String declaring that the player drew count ticket(s)
     *
     * @param count (int) number of drew ticket
     * @return (String) a String declaring that the player drew count ticket(s)
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a String declaring that the player drew a card from the deck
     *
     * @return (String) a String declaring that the player drew a card from the deck
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Returns a String declaring that the player drew the given card from the face up cards
     *
     * @param card (String) drew by the player
     * @return a String declaring that the player drew the given card from the face up cards
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Returns a String declaring that the player claimed the given route with the given cards
     *
     * @param route (Route) Route claimed by the player
     * @param cards (SortedBag<Card>) Cards used by the player to claim the route
     * @return (String) a String declaring that the player claimed the given route with the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeName(route), cardDescription(cards));
    }

    /**
     * Returns a String declaring that the player try to claim the given route with the given cards
     *
     * @param route (Route) Route that the player try to claim
     * @param initialCards (SortedBag<Card>) Cards used by the player to try to claim the route
     * @return (String) a String declaring that the player try to claim the given route with the given cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardDescription(initialCards));
    }

    /**
     * Returns a String declaring that the player drew the 3 given additionnal cards and that it generate an additional cost of additionalCost
     *
     * @param drawnCards (SortedBag<Card>) 3 Cards drew by the player
     * @param additionalCost (int) additional cost generated by the drawn cards
     * @return (String) a String declaring that the player drew the 3 given additionnal cards and that it generate an additional cost of additionalCost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        return additionalCost == 0 ?
                String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardDescription(drawnCards))
                        + StringsFr.NO_ADDITIONAL_COST
                :
                String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardDescription(drawnCards))
                        + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost));

    }

    /**
     * Returns a String declaring that the player didn't claim the given tunnel
     *
     * @param route (Route) Tunnel that the player tried to claim
     * @return (String) a String declaring that the player didn't player the given tunnel
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    /**
     * Returns a String declaring that the player has less than 2 cars and that the last turn begins
     *
     * @param carCount (int) number of remaining card
     * @return (String) a String declaring that the player has less than 2 cars and that the last turn begins
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     * Returns a String declaring that the player gets a bonus thanks to the given trail (Longest of the game)
     *
     * @param longestTrail (Trail) longest trail of the game
     * @return (String) a String declaring that the player gets a bonus thanks to the given trail (Longest of the game)
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + EN_DASH_SEPARATOR + longestTrail.station2());
    }

    /**
     * Return a String declaring that the player won the game with (points) points and that its opponent has (loserPoints) points
     *
     * @param winnerPoints (int) number of points of the player
     * @param loserPoints (int) number of points that the opponent loose
     * @return (String) a String declaring that the player won the game with (points) points and that its opponent has (loserPoints) points
     */
    public String won(int winnerPoints, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, winnerPoints, plural(winnerPoints), loserPoints, plural(loserPoints));
    }
}
