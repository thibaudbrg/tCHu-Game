package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * Represents the state of the game tChu
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class GameState extends PublicGameState {
    private final CardState completeCardState;
    private final Deck<Ticket> ticketsDeck;
    private final Map<PlayerId, PlayerState> completePlayerState;

    private GameState(CardState cardState, PlayerId currentPlayerId, Map<PlayerId,
            PlayerState> playerState, Deck<Ticket> ticketsDeck, PlayerId lastPlayer) {

        super(ticketsDeck.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.ticketsDeck = ticketsDeck;
        completePlayerState = Map.copyOf(playerState);
        completeCardState = cardState;
    }

    /**
     * Returns the initial state of a game of tCHu in which the ticket deck contains the given tickets
     * and the card deck contains all the cards in the game without the top 8 (those dealt to the player).
     * These cards are shuffled by a random generator, which is also used to randomly
     * select the identity of the first player
     *
     * @param tickets (SortedBag<Ticket>) The tickets for the ticket deck.
     * @param rng     (Random) The random generator
     * @return (GameState) The initial state of a game of tCHu in which the ticket deck contains the given tickets
     * and the card deck contains all the cards in the game without the top 8
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(2));

        Deck<Card> cardsDeck = Deck.of(Constants.ALL_CARDS, rng);
        Map<PlayerId, PlayerState> map = new HashMap<>();

        for (PlayerId id : PlayerId.ALL) {
            map.put(id, PlayerState.initial(cardsDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
            cardsDeck = cardsDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }
        return new GameState(CardState.of(cardsDeck), firstPlayer, map,
                Deck.of(tickets, rng), null);
    }


    /**
     * Returns all part of the player state of the given player
     *
     * @param playerId (PlayerState) The given player Id
     * @return (PlayerState) The all part of the player state of the given player
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return completePlayerState.get(playerId);
    }

    /**
     * Returns the all part of the current player's state
     *
     * @return (PlayerState) The all part of the current player's state
     */
    @Override
    public PlayerState currentPlayerState() {
        return completePlayerState.get(currentPlayerId());
    }

    /**
     * Return the count tickets from the top of the deck
     *
     * @param count (int) The number of tickets
     * @return (SortedBag < Ticket >) The count notes from the top of the deck
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= ticketsDeck.size());
        return ticketsDeck.topCards(count);
    }

    /**
     * Returns an identical state to the receiver, but without the count tickets from the top of the deck
     *
     * @param count (int) the number of tickets
     * @return (GameState) An identical state to the receiver, but without the count tickets from the top of the deck
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= ticketsDeck.size());
        return new GameState(completeCardState, currentPlayerId(), completePlayerState,
                ticketsDeck.withoutTopCards(count), lastPlayer());
    }

    /**
     * Returns the card to the top of the deck
     *
     * @return (Card) The card to the top of the deck
     */
    public Card topCard() {
        Preconditions.checkArgument(!completeCardState.isDeckEmpty());
        return completeCardState.topDeckCard();
    }

    /**
     * Returns a state identical to the receiver but without the top card of the deck
     *
     * @return (GameState) A state identical to the receiver but without the top card of the deck
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!completeCardState.isDeckEmpty());
        return new GameState(completeCardState.withoutTopDeckCard(), currentPlayerId(),
                completePlayerState, ticketsDeck, lastPlayer());
    }

    /**
     * Returns a state identical to the receiver but with the data cards added to the discard pile
     *
     * @param discardedCards (SortedBag<Card>) The discarded cards
     * @return (GameState) A state identical to the receiver but with the data cards added to the discard pile
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(completeCardState.withMoreDiscardedCards(discardedCards), currentPlayerId(),
                completePlayerState, ticketsDeck, lastPlayer());
    }

    /**
     * Returns a state identical to the receiver unless the deck is empty,
     * in which case it is recreated from the discard pile, shuffled using the given random generator
     *
     * @param rng (Random) The random generator
     * @return (GameState) A state identical to the receiver unless the deck is empty
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return (!completeCardState.isDeckEmpty()) ?
            new GameState(completeCardState, currentPlayerId(),
                    completePlayerState, ticketsDeck, lastPlayer())
        :
            new GameState(completeCardState.withDeckRecreatedFromDiscards(rng),
                    currentPlayerId(), completePlayerState, ticketsDeck, lastPlayer());
    }

    /**
     * Returns a state identical to the receiver but in which the given tickets have been added to the given player's hand
     *
     * @param playerId (PlayerId) The given player Id
     * @param chosenTickets (SortedBag<Tickets>) Tickets that the player chose to keep.
     * @return (GameState) a state identical to the receiver but in which the given tickets have been added to the given player's hand
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).tickets().isEmpty());

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(completePlayerState);
        newPlayerState.replace(playerId, completePlayerState.get(playerId).withAddedTickets(chosenTickets));

        return new GameState(completeCardState, currentPlayerId(),
                newPlayerState, ticketsDeck, lastPlayer());
    }

    /**
     * Returns a state identical to the receiver, but in which the current player has drawn the drawnTickets
     * from the top of the deck, and chosen to keep those contained in chosenTicket
     *
     * @param drawnTickets  (SortedBag<Ticket>) drawn Tickets from the top of the deck
     * @param chosenTickets (SortedBag<Ticket>) Tickets chosen to keep by the player
     * @return (GameState) a state identical to the receiver, but in which the current player has drawn the drawnTickets from
     * the top of the deck, and chosen to keep those contained in chosenTicket
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(completePlayerState);
        newPlayerState.replace(currentPlayerId(),
                completePlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets));

        return new GameState(completeCardState, currentPlayerId(),
                newPlayerState, ticketsDeck.withoutTopCards(drawnTickets.size()), lastPlayer());
    }

    /**
     * Returns a state identical to the receiver except that the face-up card at the given location has been placed
     * in the current player's hand, and replaced by the one at the top of the deck
     *
     * @param slot (int) location of the card to place in the player's hand
     * @return (GameState) a state identical to the receiver except that the face-up card at the given location has been placed
     * in the current player's hand, and replaced by the one at the top of the deck
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(completePlayerState);
        newPlayerState.replace(currentPlayerId(),
                completePlayerState.get(currentPlayerId())
                        .withAddedCard(completeCardState.faceUpCard(slot)));

        return new GameState(completeCardState.withDrawnFaceUpCard(slot),
                currentPlayerId(), newPlayerState, ticketsDeck, lastPlayer());
    }

    /**
     * Returns a state identical to the receiver except that the top card of the deck has been placed in the current player's hand
     *
     * @return (GameState) a state identical to the receiver except that the top card of the deck has been placed in the current player's hand
     */
    public GameState withBlindlyDrawnCard() {
        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(completePlayerState);
        newPlayerState.replace(currentPlayerId(),
                completePlayerState.get(currentPlayerId()).withAddedCard(completeCardState.topDeckCard()));

        return new GameState(completeCardState.withoutTopDeckCard(), currentPlayerId(),
                newPlayerState, ticketsDeck, lastPlayer());

    }

    /**
     * returns a identical state to the receiver but in which the current player has seized the given route using the given cards.
     *
     * @param route (Route) claimed route
     * @param cards (SortedBag<Cards>) cards used to the claim the route
     * @return (GameState) returns a identical state to the receiver but in which the current player has seized the given route using the given cards.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(completePlayerState);
        newPlayerState.replace(currentPlayerId(), completePlayerState.
                get(currentPlayerId()).withClaimedRoute(route, cards));

        return new GameState(completeCardState.withMoreDiscardedCards(cards),
                currentPlayerId(), newPlayerState, ticketsDeck, lastPlayer());

    }

    /**
     * returns true if the last turn begins,
     * i.e. if the identity of the last player is currently unknown but the current player has only two or fewer cars left
     *
     * @return (boolean) true if the last turn begins
     */
    public boolean lastTurnBegins() {
        return (lastPlayer() == null && currentPlayerState().carCount() <= 2);
    }

    /**
     * Ends the turn of the current player,
     * i.e. returns a state identical to the receiver except that the current player is the one following the current player;
     * moreover, if lastTurnBegins returns true, the current player becomes the last player.
     *
     * @return (GameState) a state identical to the receiver except that the current player is the one following the current player
     */
    public GameState forNextTurn() {
        return lastTurnBegins() ?
                new GameState(completeCardState, currentPlayerId().next(),
                        completePlayerState, ticketsDeck, currentPlayerId())
                :
                new GameState(completeCardState, currentPlayerId().next(),
                        completePlayerState, ticketsDeck, lastPlayer());
    }
}
