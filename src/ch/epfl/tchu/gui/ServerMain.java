package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Contains the main program of the tCHu server.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public class ServerMain extends Application {
    static void main(String[] args) {
        launch(args);
    }

    /**
     * Takes care of starting the server by :
     * - analyzing the arguments passed to the program to determine the names of the two players,
     * - waiting for a connection from the client on port 5108,
     * - creating the two players, the first one being a graphical player, the second one a proxy of the remote player on the client,
     * - starting the thread that runs the game, which does nothing but execute the play method of Game
     *
     * @param primaryStage (Stage) Not used
     */
    @Override
    public void start(Stage primaryStage)  {
        List<String> argList = getParameters().getRaw();

        String player1Name = argList.size() == 2 || argList.size() == 1 ? argList.get(0) : "Ada";
        String player2Name = argList.size() == 2 ? argList.get(1) : "Charles";
        Map<PlayerId, String> playersName = Map.of(PlayerId.PLAYER_1, player1Name, PlayerId.PLAYER_2, player2Name);
        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
            GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
            RemotePlayerProxy playerProxy = new RemotePlayerProxy(socket);
            new Thread(() -> Game.play(Map.of(PlayerId.PLAYER_1, player, PlayerId.PLAYER_2, playerProxy),
                    playersName, SortedBag.of(ChMap.tickets()), new Random(2020))).start();
        } catch (IOException e){
            throw new UncheckedIOException(e);

        }
    }
}
