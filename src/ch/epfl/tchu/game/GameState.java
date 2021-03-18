package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {
    private final CardState completeCardState;
    private final Deck<Ticket> ticketsDeck;
    private final Map<PlayerId, PlayerState> completePlayerState;

    private GameState(CardState cardState, PlayerId currentPlayerId, Map<PlayerId,
            PlayerState> playerState, Deck<Ticket> ticketsDeck, PlayerId lastPlayer) {
        super(ticketsDeck.size(), cardState, currentPlayerId, makePublic(playerState), null);
        this.ticketsDeck = ticketsDeck;
        completePlayerState = playerState;
        completeCardState = cardState;


    }

    public static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        Map<PlayerId, PublicPlayerState> publicPlayerState = Map.copyOf(playerState);
        return publicPlayerState;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeckWithOutTop8 = Deck.of(Constants.ALL_CARDS, rng);

        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(1));

        Map<PlayerId, PlayerState> map = Map.of(firstPlayer, new PlayerState(SortedBag.of(), SortedBag.of(), List.of()),
                firstPlayer.next(), new PlayerState(SortedBag.of(), SortedBag.of(), List.of()));
        return new GameState(CardState.of(cardDeckWithOutTop8), firstPlayer, map, Deck.of(tickets, rng), null);
    }


    @Override
    public PlayerState playerState(PlayerId playerId) {
        return completePlayerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return completePlayerState.get(currentPlayerId());
    }

    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(0 <= count && count >= ticketsDeck.size());
        return ticketsDeck.topCards(count);
    }

    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count >= ticketsDeck.size());
        return new GameState(completeCardState, currentPlayerId(), completePlayerState,
                ticketsDeck.withoutTopCards(count), null);
    }

    public Card topCard() {
        Preconditions.checkArgument(!completeCardState.isDeckEmpty());
        return completeCardState.topDeckCard();
    }

    public GameState withoutTopCard() {
        Preconditions.checkArgument(!completeCardState.isDeckEmpty());
        return new GameState(completeCardState.withoutTopDeckCard(), currentPlayerId(), completePlayerState, ticketsDeck, null);
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(completeCardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), completePlayerState, ticketsDeck
                , null);
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (!completeCardState.isDeckEmpty()) {
            return this;
        } else
            return new GameState(completeCardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), completePlayerState, ticketsDeck, null);
    }

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).tickets().isEmpty());
        Map<PlayerId, PlayerState> newPlayerState = Map.copyOf(completePlayerState);
        newPlayerState.replace(playerId, completePlayerState.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(completeCardState, playerId, newPlayerState, ticketsDeck, null);
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> newPlayerState = Map.copyOf(completePlayerState);
        newPlayerState.replace(currentPlayerId(), completePlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(completeCardState, currentPlayerId(), newPlayerState, ticketsDeck.withoutTopCards(chosenTickets.size()), null);
    }

    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> newPlayerState = Map.copyOf(completePlayerState);
        newPlayerState.replace(currentPlayerId(), completePlayerState.get(currentPlayerId()).withAddedCard(completeCardState.faceUpCard(slot)));
        return new GameState(completeCardState.withDrawnFaceUpCard(slot), currentPlayerId(), newPlayerState, ticketsDeck, null);
    }

    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> newPlayerState = Map.copyOf(completePlayerState);
        newPlayerState.replace(currentPlayerId(), completePlayerState.get(currentPlayerId()).withAddedCard(completeCardState.topDeckCard()));
        return new GameState(completeCardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState, ticketsDeck, null);

    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        //Verifier que les cartes sont dans la main du joueur apres rendu inter
        Map<PlayerId, PlayerState> newPlayerState = Map.copyOf(completePlayerState);
        newPlayerState.replace(currentPlayerId(), completePlayerState.get(currentPlayerId()).withClaimedRoute(route, cards));
        return new GameState(completeCardState.withMoreDiscardedCards(cards), currentPlayerId(), newPlayerState, ticketsDeck, null); // Pas sur qu il fasse mettre cartes dans defausse
    }

    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    public GameState forNextTurn() {
        if (lastTurnBegins()) {
            return new GameState(completeCardState, currentPlayerId().next(), completePlayerState, ticketsDeck, currentPlayerId());
        } else
            return new GameState(completeCardState, currentPlayerId().next(), completePlayerState, ticketsDeck, null);
    }
}
