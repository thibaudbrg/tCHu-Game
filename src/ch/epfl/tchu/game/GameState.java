package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public final class GameState extends PublicGameState {

    private final Deck<Ticket> ticketsDeck;
    private final Deck<Card> cardsDeck;
    private final Map<PlayerId, PlayerState> completePlayerState;

    private GameState(PlayerId currentPlayerId, Map<PlayerId,
            PlayerState> playerState, Deck<Ticket> ticketsDeck, Deck<Card> cardsDeck) {
        super(ticketsDeck.size(), CardState.of(cardsDeck), currentPlayerId, makePublic(playerState), null);
        this.ticketsDeck = ticketsDeck;
        this.cardsDeck = cardsDeck;
        completePlayerState = playerState;


    }

    public static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        Map<PlayerId, PublicPlayerState> publicPlayerState = Map.copyOf(playerState);
        return publicPlayerState;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeckWithOutTop8 = Deck.of(Constants.ALL_CARDS, rng);

        PlayerState playerState1 = PlayerState.initial(cardDeckWithOutTop8.topCards(4));
        cardDeckWithOutTop8 = cardDeckWithOutTop8.withoutTopCards(4);

        PlayerState playerState2 = PlayerState.initial(cardDeckWithOutTop8.topCards(4));
        cardDeckWithOutTop8 = cardDeckWithOutTop8.withoutTopCards(4);

        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(1));

        Map<PlayerId, PlayerState> map = new TreeMap();
        map.put(firstPlayer, playerState1);
        map.put(firstPlayer.next(), playerState2);
        return new GameState(firstPlayer, map, Deck.of(tickets, rng), cardDeckWithOutTop8);
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
        return new GameState(currentPlayerId(),completePlayerState,
                ticketsDeck.withoutTopCards(count),cardsDeck);
    }

    public Card topCard() {
        Preconditions.checkArgument(!cardsDeck.isEmpty());
        return cardsDeck.topCard();
    }

    public GameState withoutTopCard() {
        Preconditions.checkArgument(cardsDeck.isEmpty());
        return new GameState(currentPlayerId(),completePlayerState, ticketsDeck,
                cardsDeck.withoutTopCard());
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(ticketsCount(), cardState() ,currentPlayerId(),completePlayerState, ticketsDeck,
                cardsDeck.toSortedBag);
    }


}
