package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Represents a tCHu player
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public interface Player {

    /**
     * Represents the three types of actions a tCHu player can perform during a turn
     */
    enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public final static List<TurnKind> ALL = List.of(values());
    }

    /**
     *  Called at the beginning of the game to give the player his ownId identity,
     *  as well as the names of the different players, including his own, which are in playerNames
     *
     * @param ownId (PlayerId) The own identity to the player
     * @param playerNames (Map<PlayerId, String>) The names of the players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Called each time a piece of information must be communicated to the player during the game;
     * this information is given in the form of a character string, usually produced by the Info
     *
     * @param info (String) The information
     */
    void receiveInfo(String info);

    /**
     * Called whenever the state of the game has changed, to inform the player of the public component of this new state,
     * newState, as well as its own state, ownState
     * @param newState (PublicGameState) The new state
     * @param ownState (PlayerState) The own state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Called at the beginning of the game to give the player the five tickets that have been dealt to him
     *
     * @param tickets (SortedBag<Ticket>) The tickets
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Called at the beginning of the game to ask the player which of the tickets they were initially dealt to keep
     * @return (SortedBag<Ticket>) The tickets that the player keeps
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Called at the beginning of a player's turn, to find out what type of action they wish to perform during that turn
     *
     * @return (TurnKind) The type of action the player wishes to perform
     */
    TurnKind nextTurn();

    /**
     * Called when the player has decided to draw additional tickets during the game,
     * in order to inform him of the tickets drawn and which ones he keeps
     *
     * @param options (SortedBad<Ticket>) the option tickets
     * @return (SortedBag<Ticket>) chosen Tickets
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Called when the player has decided to draw railcar/locomotive cards,
     * so that he knows where he wants to draw them from: from one of the slots containing a face-up card or the deck
     *
     * @return (int) The value is between 0 and 4 if it comes from a slot containin a face-up card,
     * or the dummy slot number designating the deck of cards
     */
    int drawSlot();

    /**
     * Called when the player has decided to (try to) seize a road, in order to know which road it is
     *
     * @return (Route) The Route the player wants to seize
     */
    Route claimedRoute();

    /**
     * Called when the player has decided to (try to) seize a road,
     * in order to know which card(s) he initially wants to use for this
     *
     * @return (SortedBag<Card>) The Cards he initially wants to use
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Called when the player has decided to try to claim a tunnel and additional cards are needed,
     * in order to know which card(s) he wants to use for this, the possibilities being passed to him as an argument;
     * if the returned multiset is empty, it means that the player does not want to (or cannot) choose one of these possibilities
     *
     * @param options (List<SortedBag<Card>>) The possibilities to claim a tunnel
     * @return (SortedBag<Card>) The cards the player choose
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
