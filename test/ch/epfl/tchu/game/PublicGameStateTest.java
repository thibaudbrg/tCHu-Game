package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
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
        var s9 = new Station(9, "FD");
        var s10 = new Station(10, "S");
        var route5 = new Route("4", s9, s10, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        var s11 = new Station(11, "ZOO");
        var s12= new Station(12, "Lausa");
        var route6 = new Route("3", s11, s12, 5, Route.Level.OVERGROUND, Color.ORANGE);

        publicPlayerState.put(PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of(route3,route5)));
        publicPlayerState.put(PlayerId.PLAYER_1, new PublicPlayerState(3, 3, List.of(route4, route3,route6)));

        PublicGameState s = new PublicGameState(5, CardState.of(Deck.of(Constants.ALL_CARDS, new Random(21))), PlayerId.PLAYER_2, publicPlayerState, null);
        List<Route> l = s.claimedRoutes();
        assertEquals(List.of(route3, route4, route3).toString(), s.claimedRoutes().toString());
    }

    @Test
    void constructrFails(){
        Map<PlayerId, PublicPlayerState> publicPlayerState = new HashMap<>();
        Map<PlayerId, PublicPlayerState> publicPlayerState1 = new HashMap<>();
        var s7 = new Station(6, "Christian");
        var s8 = new Station(7, "Grey");
        var route4 = new Route("4", s7, s8, 1, Route.Level.OVERGROUND, Color.BLUE);
        var s5 = new Station(4, "Lille");
        var s6 = new Station(5, "Morteau");
        var route3 = new Route("3", s5, s6, 4, Route.Level.OVERGROUND, Color.GREEN);
        var s9 = new Station(9, "FD");
        var s10 = new Station(10, "S");
        var route5 = new Route("4", s9, s10, 2, Route.Level.UNDERGROUND, Color.YELLOW);
        var s11 = new Station(11, "ZOO");
        var s12= new Station(12, "Lausa");
        var route6 = new Route("3", s11, s12, 5, Route.Level.OVERGROUND, Color.ORANGE);

        publicPlayerState.put(PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of(route3,route5)));
        publicPlayerState.put(PlayerId.PLAYER_1, new PublicPlayerState(3, 3, List.of(route3,route4,route5)));
        publicPlayerState1.put(PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of(route3,route5)));
        assertThrows(IllegalArgumentException.class,()->{ PublicGameState s = new PublicGameState(-1, CardState.of(Deck.of(Constants.ALL_CARDS, new Random(21))), PlayerId.PLAYER_2, publicPlayerState, null);
          });
        assertThrows(IllegalArgumentException.class,()->{ PublicGameState s = new PublicGameState(3, CardState.of(Deck.of(Constants.ALL_CARDS, new Random(21))), PlayerId.PLAYER_2, publicPlayerState1, null);
        });
        assertThrows(IllegalArgumentException.class,()->{ PublicGameState s = new PublicGameState(3, CardState.of(Deck.of(Constants.ALL_CARDS, new Random(21))), null, publicPlayerState, null);
        });
        assertThrows(NullPointerException.class,()->{ PublicGameState s = new PublicGameState(3, null, PlayerId.PLAYER_1, publicPlayerState, null);
        });
    }

}