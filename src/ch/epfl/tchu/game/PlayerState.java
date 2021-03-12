package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PlayerState extends PublicPlayerState {

    private SortedBag<Ticket> tickets;
    private SortedBag<Card> cards;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, this.routes());
    }

    public SortedBag<Card> cards() {
        return this.cards;
    }

    public PlayerState withAddedCard(Card card) {
        return new PlayerState(this.tickets, this.cards.union(SortedBag.of(card)), this.routes());
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(this.tickets, this.cards.union(additionalCards), this.routes());
    }

    public boolean canClaimRoute(Route route) {
        boolean haveTheCards = false;
        for (SortedBag s : route.possibleClaimCards()) {
            if (cards.contains(s)) haveTheCards = true;
        }
        return haveTheCards && route.length() <= this.carCount();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());
        return route.possibleClaimCards();
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(initialCards.isEmpty());
        Preconditions.checkArgument(drawnCards.size() == 3);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag<Card> remainingUsableCard = builder.add(cards.countOf(Card.LOCOMOTIVE), Card.LOCOMOTIVE).
                add(cards.countOf(Card.of(initialCards.get(0).color())), Card.of(initialCards.get(0).color())).build().difference(initialCards);
        List<SortedBag<Card>> possibleAddCards = new ArrayList<>(remainingUsableCard.subsetsOfSize(additionalCardsCount));
        possibleAddCards.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return possibleAddCards;


    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routesCopy = new ArrayList<>(this.routes());
        routesCopy.add(route);

        return new PlayerState(this.tickets, this.cards.difference(claimCards), routesCopy);
    }

    public int ticketPoints() {
        int points = 0;
        for (Ticket ticket : tickets) {
            // TODO COMPLETE
        }
    }

    public int finalPoints() {
        return this.claimPoints() + ticketPoints();
    }


}
