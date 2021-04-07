package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Map;
import java.util.TreeMap;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

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
     * @param ticketsCount    number of tickets in the deck
     * @param cardState       the public state of the car/locomotive cards
     * @param currentPlayerId current player
     * @param playerState     public player states of the players
     * @param lastPlayer      the last player id
     * @throws NullPointerException if currentPlayerId or cardState is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) throws NullPointerException {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = new TreeMap<PlayerId, PublicPlayerState>(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * Returns the number of tickets in the deck
     *
     * @return the number of tickets in the deck
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Returns true if the tickets deck isn't empty
     *
     * @return true if the tickets deck isn't empty
     */
    public boolean canDrawTickets() {
        return ticketsCount > 0;
    }

    /**
     * Returns the public state of the car/locomotive cards
     *
     * @return the public state of the car/locomotive cards
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Returns true iff the deck and the discards contains at least 5 cards.
     *
     * @return true iff the deck and the discards contains at least 5 cards.
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * Returns the current player Id
     *
     * @return the current player Id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Returns the public part of the player state of the given player
     *
     * @param playerId given player Id
     * @return the public part of the player state of the given player
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the public part of the current player's state
     *
     * @return the public part of the current player's state
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Returns all the claimed road of the game
     *
     * @return all the claimed road of the game
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        claimedRoutes.addAll(0, currentPlayerState().routes());
        claimedRoutes.addAll(claimedRoutes.size(), playerState(currentPlayerId.next()).routes());
        return claimedRoutes;
    }

    /**
     * Returns the last player Id
     *
     * @return the last player Id
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
