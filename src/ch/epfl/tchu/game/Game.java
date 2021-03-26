package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game {

    static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playerNames.size() == 2);
        Preconditions.checkArgument(players.size() == 2);


        initPlayer;

    }

    //TODO receiveInfo
    //TODO updateState

}
