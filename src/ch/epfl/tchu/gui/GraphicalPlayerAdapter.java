package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
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
        Platform.runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
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

        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, ticket -> {
            try {
                sortedBagTQueue.put(ticket);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return sortedBagTQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public TurnKind nextTurn() {

        Platform.runLater(() -> {
            graphicalPlayer.startTurn((() -> {
                try {
                    turnKindsQueue.put(TurnKind.DRAW_TICKETS);
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }), ((i) -> {
                try {
                    turnKindsQueue.put(TurnKind.DRAW_CARDS);
                    cardsQueue.put(i);
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }), ((r, cards) -> {
                try {
                    turnKindsQueue.put(TurnKind.CLAIM_ROUTE);
                    routeQueue.put(r);
                    sortedBagCQueue.put(cards);

                } catch (InterruptedException e) {
                    throw new Error();
                }
            }));


        });
        try {
            return turnKindsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();

        }
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
            Platform.runLater(() -> graphicalPlayer.drawCard(slot -> {
                try {
                    cardsQueue.put(slot);
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }));
            try {
                return cardsQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }
    }


    @Override
    public Route claimedRoute() {
        try {
            return routeQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return sortedBagCQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }

    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(() -> graphicalPlayer.chooseAdditionalCards(options, (c) -> {
            try {
                sortedBagCQueue.put(c);
            } catch (InterruptedException e) {
                throw new Error();
            }

        }));
        try {
            return sortedBagCQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }

    }
}
