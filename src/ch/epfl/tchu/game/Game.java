package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.Map;
import java.util.Random;

public final class Game {

    static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playerNames.size() == 2);
        Preconditions.checkArgument(players.size() == 2);
        players.forEach((Id, p) -> {
            p.initPlayers(Id, playerNames);
        });
        GameState gameState = GameState.initial(tickets, rng);
        Game.sendInfoToBothPlayers(players, );


        players.forEach((Id, p) -> {
            p.setInitialTicketChoice(tickets);
            p.chooseInitialTickets();
        });

        players.forEach((Id, p) -> {
            sendInfoToBothPlayers(p, );
        });


        //The Game starts
        gameState.
    }

    private static void sendInfoToBothPlayers(Map<PlayerId, Player> players, String s) {
        players.forEach((Id, p) -> {
            p.receiveInfo(s);
        });
    }

    private static void updateStateBothPlayers(Map<PlayerId, Player> players, PublicGameState newState) {
        players.forEach((Id, p) -> {
            p.updateState(newState, );
        });
    }
    //TODO updateState

}
