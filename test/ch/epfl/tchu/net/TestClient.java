package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
private int i = 0;
        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.printf("info: %s\n", info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.printf("newState : ticket %d \n",newState.ticketsCount());
            System.out.printf("PlayerState points %d\n",ownState.finalPoints());
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
System.out.println(tickets);
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return SortedBag.of(2,ChMap.tickets().get(10),3,ChMap.tickets().get(5));
        }

        @Override
        public TurnKind nextTurn() {
            i+=2;
            return TurnKind.ALL.get(i%3);
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.println(options);

            return options;
        }

        @Override
        public int drawSlot() {
            return i%5;
        }

        @Override
        public Route claimedRoute() {
            return ChMap.routes().get(i%5);
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return null;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return null;
        }


    }


}