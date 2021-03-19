package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PublicGameStateTest {

    @Test
    void claimedRoutes() {
        Map<PlayerId, PublicPlayerState> publicPlayerState = new HashMap<>();
        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);
        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);

        publicPlayerState.put(PlayerId.PLAYER_1, new PublicPlayerState(3,3, List.of(route4,route3)));
        publicPlayerState.put(PlayerId.PLAYER_2, new PublicPlayerState(3,3, List.of(route3)));

        PublicGameState s =new PublicGameState(5,CardState.of(Deck.of(Constants.ALL_CARDS,new Random(21))),PlayerId.PLAYER_1,publicPlayerState,null);
List<Route> l = s.claimedRoutes();
        assertEquals(List.of(route3,route4).toString(),s.claimedRoutes().toString());
    }
}