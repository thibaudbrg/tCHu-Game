package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);

            Info i = new Info("Charles");
            playerProxy.receiveInfo(i.canPlay());



            var faceUpCards = SortedBag.of(5, Card.LOCOMOTIVE).toList();
            var cardState = new PublicCardState(faceUpCards, 0, 0);
            var initialPlayerState = (PublicPlayerState) PlayerState.initial(SortedBag.of(4, Card.RED));
            var playerState = Map.of(
                    PLAYER_1, initialPlayerState,
                    PLAYER_2, initialPlayerState);
            var ticketsCount = 2;
            var p1 = PLAYER_1;
            PublicGameState publicGameState = new PublicGameState(ticketsCount, cardState, p1, playerState, p1);

            var builder = new SortedBag.Builder<Card>();
            builder.add(2, Card.BLUE);
            builder.add(32, Card.GREEN);
            builder.add(2, Card.LOCOMOTIVE);
            builder.add(5, Card.BLACK);
            builder.add(8, Card.YELLOW);
            builder.add(1, Card.GREEN);
            PlayerState ownState = playerState.initial(builder.build());
            // playerProxy.updateState(publicGameState, ownState);

        }
        System.out.println("Server done!");
    }
}