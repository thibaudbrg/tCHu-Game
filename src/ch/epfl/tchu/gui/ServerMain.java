package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerMain extends Application {
    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> argList = getParameters().getRaw();

        String player1Name = argList.size()==2?argList.get(0):"Ada";
        String player2Name = argList.size()==2?argList.get(1):"Charles";
        Map<PlayerId,String> playersName = Map.of(PlayerId.PLAYER_1,player1Name, PlayerId.PLAYER_2,player2Name);
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            GraphicalPlayerAdapter player =  new GraphicalPlayerAdapter(); //TODO ON SAIT PAS QUI JOUE EN LOCAL
            RemotePlayerProxy playerProxy =new RemotePlayerProxy(socket);
            Game.play(Map.of(PlayerId.PLAYER_1,player,PlayerId.PLAYER_2,playerProxy),playersName, SortedBag.of(ChMap.tickets()),new Random(2020));

        }

/*
        Game holaquetal = new Game();
        holaquetal.play();


        java.util.Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag< Ticket > tickets, Random
        rng

 */
    }
}
