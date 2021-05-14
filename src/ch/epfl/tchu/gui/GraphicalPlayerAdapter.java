package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Adapts an instance of GraphicalPlayer into a value of type Player
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer; // peut etre final
    private final BlockingQueue<SortedBag<Ticket>> sortedBagTQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<TurnKind> turnKindsQueue;
    private final BlockingQueue<Integer> cardsQueue;
    private final BlockingQueue<SortedBag<Card>> sortedBagCQueue;

    public GraphicalPlayerAdapter() {
        sortedBagTQueue = new ArrayBlockingQueue<>(1);
        routeQueue = new ArrayBlockingQueue<>(1);
        turnKindsQueue = new ArrayBlockingQueue<>(1);
        cardsQueue = new ArrayBlockingQueue<>(1);
        sortedBagCQueue = new ArrayBlockingQueue<>(1);
    }

    @Override
    /**
     *  Builds, on the JavaFX thread, the instance of the graphical player GraphicalPlayer that it adapts
     *
     * @param ownId (PlayerId) The own identity to the player
     * @param playerNames (Map<PlayerId, String>) The names of the players
     */
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames));

    }

    @Override
    /**
     * Calls, on the JavaFX thread, the method of the same name of the graphic player
     *
     * @param info (String) The information
     */
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    /**
     * Calls, on the JavaFX thread, the setState method of the graphic player
     *
     * @param newState (PublicGameState) The new state
     * @param ownState (PlayerState) The own state
     */
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    /**
     * Calls, on the JavaFX thread, the chooseTickets method of the graphical player,
     * to ask him to choose his initial tickets, passing him a choice handler that stores
     * the player's choice in a blocking queue
     *
     * @param tickets (SortedBag<Ticket>) The tickets
     */
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, ticket -> put(sortedBagTQueue, ticket)));
    }

    @Override
    /**
     * Blocks until the queue also used by setInitialTicketChoice contains a value, then returns it
     * @return (SortedBag < Ticket >) The tickets that the player keeps
     */
    public SortedBag<Ticket> chooseInitialTickets() {
        return take(sortedBagTQueue);
    }

    @Override
    /**
     * Calls, on the JavaFX thread, the startTurn method of the graphical player,
     * passing it action handlers that place the type of turn chosen, as well as the possible "arguments" of the action
     *
     * @return (TurnKind) The type of action the player wishes to perform
     */
    public TurnKind nextTurn() {
        Platform.runLater(() -> graphicalPlayer.startTurn(
                (() -> put(turnKindsQueue, TurnKind.DRAW_TICKETS)),
                ((i) -> put(turnKindsQueue, TurnKind.DRAW_CARDS)),
                ((r, cards) -> put(turnKindsQueue, TurnKind.CLAIM_ROUTE))));
        return take(turnKindsQueue);
    }

    @Override
    /**
     * Chains the actions performed by setInitialTicketChoice and chooseInitialTickets
     *
     * @param options (SortedBad<Ticket>) the option tickets
     * @return (SortedBag < Ticket >) chosen Tickets
     */
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }


    @Override
    /**
     *  tests if the queue containing the card locations contains a value; if it does,
     *  it means that drawSlot is called for the first time of the round,
     *  and that the handler installed by nextTurn has placed the location of the first card drawn in this queue,
     *  otherwise, it means that drawSlot is called for the second time of the turn
     *
     * @return (int) The value is between 0 and 4 if it comes from a slot containin a face-up card,
     * or the dummy slot number designating the deck of cards
     */
    public int drawSlot() {
        if (!cardsQueue.isEmpty()) {
            return cardsQueue.remove();
        } else {
            Platform.runLater(() -> graphicalPlayer.drawCard(slot -> put(cardsQueue, slot)));
            return take(cardsQueue);
        }
    }


    @Override
    /**
     * Extracts and returns the first element of the queue containing the routes,
     * which will have been placed there by the handler passed to startTurn by nextTurn
     *
     * @return (Route) The Route the player wants to seize
     */
    public Route claimedRoute() {
        return take(routeQueue);
    }

    @Override
    /**
     * Is similar to claimedRoute but uses the queue containing the multisets of maps
     *
     * @return (SortedBag < Card >) The Cards he initially wants to use
     */
    public SortedBag<Card> initialClaimCards() {
        return take(sortedBagCQueue);

    }

    @Override
    /**
     * Calls, on the JavaFX thread, the method of the same name of the graphical player, and then blocks
     * while waiting for an element to be placed in the queue containing the multi-sets of cards,
     * which it returns.
     *
     * @param options (List<SortedBag<Card>>) The possibilities to claim a tunnel
     * @return (SortedBag < Card >) The cards the player choose
     */
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(() -> graphicalPlayer.chooseAdditionalCards(options, (c) -> put(sortedBagCQueue, c)));
        return take(sortedBagCQueue);
    }

    private <T> void put(BlockingQueue<T> queue, T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    private <T> T take(BlockingQueue<T> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
