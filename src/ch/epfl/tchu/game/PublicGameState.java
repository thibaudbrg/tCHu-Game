package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Represents the public state of the game Tchu.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */

public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor the public state of a game of tChu
     *
     * @param ticketsCount    (int) number of tickets in the deck
     * @param cardState       (PublicCardState) the public state of the car/locomotive cards
     * @param currentPlayerId (PlayerId) current player
     * @param playerState     (Map<PlayerId, PublicPlayerState>) public player states of the players
     * @param lastPlayer      (PlayerId) the last player id
     * @throws NullPointerException if currentPlayerId or cardState is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * Returns the number of tickets in the deck
     *
     * @return (int) the number of tickets in the deck
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Returns true if the tickets deck isn't empty
     *
     * @return (boolean) true if the tickets deck isn't empty
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * Returns the public state of the car/locomotive cards
     *
     * @return (PublicCardState) the public state of the car/locomotive cards
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Returns true iff the deck and the discards contains at least 5 cards.
     *
     * @return (boolean) true iff the deck and the discards contains at least 5 cards.
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * Returns the current player Id
     *
     * @return (PlayerState) the current player Id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Returns the public part of the player state of the given player
     *
     * @param playerId (PlayerId) given player Id
     * @return (PublicPlayerState) the public part of the player state of the given player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the public part of the current player's state
     *
     * @return (PublicPlayerState) the public part of the current player's state
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Returns all the claimed road of the game
     *
     * @return (List < Route >) all the claimed road of the game
     */
    public List<Route> claimedRoutes() {

        List<Route> claimedRoutes = new ArrayList<>();
        PlayerId.ALL.forEach(c -> claimedRoutes.addAll(playerState(c).routes()));
        return claimedRoutes;
    }

    /**
     * Returns the last player Id
     *
     * @return (PlayerId) the last player Id
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }


}
