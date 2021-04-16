package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the complete state of a player
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;



    /**
     * Returns the initial state of a player to whom the given initial cards have been dealt;
     * in this initial state, the player does not yet have any tickets, and has not seized any roads
     *
     * @param initialCards (SortedBag<Card>) The initialCards
     * @return (PlayerState) The initial state of a player to whom the given initial cards have been dealt
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Constructs the state of a player with the given tickets, cards and routes
     *
     * @param tickets (SortedBag<Tickets>) The tickets of the player
     * @param cards   (SortedBag<Card>) The cards of the player
     * @param routes  (List<Routes>) The roads that the player has taken over
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }


    /**
     * Returns a state identical to the receiver, except that the player also has the given tickets
     *
     * @param newTickets (SortedBag<Tickets>) The given Tickets
     * @return (PlayerState) a state identical to the receiver, except that the player also has the given tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }


    /**
     * Returns a state identical to the receiver, except that the player also has the given card
     *
     * @param card (Card) The given card
     * @return (PlayerState) a state identical to the receiver, except that the player also has the given card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Returns a state identical to the receiver, except that the player also has the given cards
     *
     * @param additionalCards (SortedBag<Card>) The given cards
     * @return (PlayerState) a state identical to the receiver, except that the player also has the given cards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    /**
     * Returns true if the player can take the given road, i.e. if he has enough wagons left and if he has the necessary cards
     *
     * @param route (Route) The given Route
     * @return (boolean) true if the player can take the given road, i.e. if he has enough wagons left and if he has the necessary cards
     */
    public boolean canClaimRoute(Route route) {
        boolean haveTheCards = route.possibleClaimCards().stream()
                .anyMatch(cards::contains);

        return haveTheCards && (route.length() <= this.carCount());
    }

    /**
     * Returns a list of all the sets of cards that the player could use to take possession of the given route
     *
     * @param route (Route) The given Route
     * @return (List<SortedBag<Card>>) a list of all the sets of cards that the player could use to take possession of the given route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());
        List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards().stream()
                .filter(cards::contains)
                .collect(Collectors.toList());

        return possibleClaimCards;
    }

    /**
     * Returns a list of all the sets of cards the player could use to take over a tunnel,
     * sorted in ascending order of the number of locomotive cards, knowing that he initially laid down the initialCards,
     * that the 3 cards drawn from the top of the deck are drawnCards, and that these force the player
     * to lay down additionalCardsCount cards
     *
     * @param additionalCardsCount (int) The number of additional cards that the player must lay down
     * @param initialCards         (SortedBag<Card>) The initial Cards
     * @param drawnCards           (SortedBag<Card>) The drawn Cards
     * @return (List<SortedBag<Card>>) a list of all the sets of cards the player could use to take over a tunnel
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty());
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);

        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        if (initialCards.get(0).color() != null) {
            builder.add(cards.countOf(Card.of(initialCards.get(0).color())),
                    Card.of(initialCards.get(0).color()));
        }

        SortedBag<Card> remainingUsableCard =
                builder.add(cards.countOf(Card.LOCOMOTIVE),
                        Card.LOCOMOTIVE).build().difference(initialCards);

        if (remainingUsableCard.size() < additionalCardsCount) return List.of();

        List<SortedBag<Card>> possibleAddCards =
                new ArrayList<>(remainingUsableCard.subsetsOfSize(additionalCardsCount));

        possibleAddCards.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return possibleAddCards;


    }

    /**
     * Returns an identical state to the receiver,
     * except that the player has additionally seized the given route with the given cards
     *
     * @param route      (Route) The given Route
     * @param claimCards (SortedBag<Card>) The cards that enable the player to own the route
     * @return (PlayerState) an identical state to the receiver,
     * except that the player has additionally seized the given route with the given cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routesCopy = new LinkedList<>(this.routes());
        routesCopy.add(route);

        return new PlayerState(tickets, cards.difference(claimCards), routesCopy);
    }

    /**
     * Return all the points obtained by the player with its tickets
     *
     * @return (int) all the points obtained by the player with its tickets
     */
    public int ticketPoints() {
        StationPartition.Builder builder = new StationPartition.Builder(maxId() + 1);

        this.routes().forEach(r -> builder.connect(r.station1(), r.station2()));
        StationPartition playerPartition = builder.build();

        int points = 0;
        for (Ticket ticket : tickets) {
            points += ticket.points(playerPartition);
        }

        return points;
    }

    /**
     * Return  all the point obtained by the player
     *
     * @return (int) all the point obtained by the player
     */
    public int finalPoints() {
        return this.claimPoints() + ticketPoints();
    }


    /**
     * Returns all the cards owned by the player
     *
     * @return (SortedBag<Card>) all the cards owned by the player
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Returns all the tickets owned by the player
     *
     * @return (SortedGag<Ticket>) all the tickets owned by the player
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    private int maxId(){
        int maxId = 0;

        for (Route route : this.routes()) {
            for (Station station : route.stations()) {
                maxId = Integer.max(maxId, station.id());
            }
        }
        return maxId;

    }

}
