package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer; // peute etre final
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
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames));

    }

    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, ticket -> put(sortedBagTQueue, ticket)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return take(sortedBagTQueue);
    }

    @Override
    public TurnKind nextTurn() {
        Platform.runLater(() -> graphicalPlayer.startTurn(
                (() -> put(turnKindsQueue, TurnKind.DRAW_TICKETS)),
                ((i) -> put(turnKindsQueue, TurnKind.DRAW_CARDS)),
                ((r, cards) -> put(turnKindsQueue, TurnKind.CLAIM_ROUTE))));
        return take(turnKindsQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }


    @Override
    public int drawSlot() {
        if (!cardsQueue.isEmpty()) {
            return cardsQueue.remove();
        } else {
            Platform.runLater(() -> graphicalPlayer.drawCard(slot -> put(cardsQueue, slot)));
            return take(cardsQueue);
        }
    }


    @Override
    public Route claimedRoute() {
        return take(routeQueue);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return take(sortedBagCQueue);

    }

    @Override
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
