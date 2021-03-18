package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) throws NullPointerException {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);
        if (currentPlayerId == null || cardState == null) {
            throw new NullPointerException();
        }

        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = new TreeMap<PlayerId, PublicPlayerState>(playerState);
        this.lastPlayer = lastPlayer;
    }


    public int ticketsCount() {
        return ticketsCount;
    }

    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    public PublicCardState cardState() {
        return cardState;
    }

    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();
        claimedRoutes.addAll(0, currentPlayerState().routes());
        claimedRoutes.addAll(claimedRoutes.size(), playerState(currentPlayerId.next()).routes());
        return claimedRoutes;
    }// Pas sur de ca TODO

    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
