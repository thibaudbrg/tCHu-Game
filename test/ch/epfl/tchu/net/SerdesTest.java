package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SerdesTest {

    @Test
    void IntTest() {
        Serde<Integer> i = Serdes.INTEGER_SERDE;
        String serde = i.serialize(2021);
        Integer deserde = i.deserialize(serde);
        assertEquals(2021, deserde);
        assertEquals("2021", serde);

    }

    @Test
    void stringTest() {
        Serde<String> i = Serdes.STRING_SERDE;
        String serde = i.serialize("Charles");
        String deserde = i.deserialize(serde);
        assertEquals("Charles", deserde);
        assertEquals("Q2hhcmxlcw==", serde);
    }

    @Test
    void stringList(){
        Serde<List<String>> i = Serdes.LIST_STRING_SERDE;
        String serde = i.serialize(List.of("Charles","BOi"));
        List<String> deserialize = i.deserialize(serde);
        assertEquals(List.of("Charles","BOi"),deserialize);
        assertEquals("Q2hhcmxlcw==,Qk9p",serde);

    }

    @Test
    void EnumTypeSerdeTest() {
        Serde<Ticket> i = Serdes.TICKET_SERDE;
        List<String> ser = new ArrayList<>();
        int a = 0;

        assertEquals(String.valueOf(2), i.serialize(ChMap.tickets().get(2)));
        assertEquals(ChMap.tickets().get(2), i.deserialize(i.serialize(ChMap.tickets().get(2))));

    }

    @Test
    void EnumClassic() {
        Serde<Card> i = Serdes.CARD_SERDE;
        assertEquals(String.valueOf(3), i.serialize(Card.ALL.get(3)));
        assertEquals(Card.ALL.get(3), i.deserialize(i.serialize(Card.ALL.get(3))));
    }

    @Test
    void ListEnumC() {
        Serde<List<Card>> i = Serdes.LIST_CARD_SERDE;
        List<Card> initial = List.of(Card.BLUE, Card.RED, Card.YELLOW, Card.WHITE, Card.WHITE, Card.LOCOMOTIVE);
        String ser = i.serialize(initial);
        List<Card> deser = i.deserialize(ser);
        assertEquals(initial, deser);
        assertEquals("2,6,4,7,7,8", ser);
    }

    @Test
    void SerdeListTest() {
        Serde<List<Route>> i = Serdes.LIST_ROUTE_SERDE;
        List<Route> initial = List.of(ChMap.routes().get(2), ChMap.routes().get(4), ChMap.routes().get(4), ChMap.routes().get(11), ChMap.routes().get(22), ChMap.routes().get(2), ChMap.routes().get(1), ChMap.routes().get(0));
        String ser = i.serialize(initial);
        List<Route> deser = i.deserialize(ser);
        assertEquals("2,4,4,11,22,2,1,0", ser);
        int a = 0;
        for (Route r : deser) {
            assertEquals(initial.get(a), r);
            a++;
        }
    }

    @Test
    void SerdeSortedBagTest() {
        Serde<SortedBag<Card>> i = Serdes.SORTEDBAG_CARD_SERDE;
        SortedBag.Builder builder = new SortedBag.Builder();
        builder.add(1, Card.BLACK);
        builder.add(8, Card.LOCOMOTIVE);
        builder.add(2, Card.BLUE);
        builder.add(3, Card.GREEN);
        SortedBag<Card> cards = builder.build();

        String ser = i.serialize(cards);
        SortedBag<Card> deser = i.deserialize(ser);

        assertEquals("0,2,2,3,3,3,8,8,8,8,8,8,8,8", ser);

        int a = 0;
        for (Card c : deser) {
            assertEquals(cards.get(a), c);
            a++;
        }
    }

    @Test
    void PublicGameStateTest() {
        Serde<PublicGameState> i = Serdes.PUBLIC_GAME_STATE_SERDE;
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", i.serialize(gs));
        PublicGameState s = i.deserialize(i.serialize(gs));

    }
@Test
void ListSortedTest(){
        Serde<List<SortedBag<Card>>> serde = Serdes.LIST_SORTEDBAG_CARD_SERDE;
        SortedBag<Card> c1 = SortedBag.of(2,Card.BLUE,3,Card.ORANGE);
    SortedBag<Card> c2 = SortedBag.of(2,Card.LOCOMOTIVE,3,Card.RED);
    SortedBag<Card> c3 = SortedBag.of(2,Card.WHITE,3,Card.BLUE);
        List<SortedBag<Card>> ls = List.of(c1,c2,c3);

        assertEquals(ls,serde.deserialize(serde.serialize(ls)));
    }
    @Test
    void PlayerStateTest() {
        Serde<PlayerState> i = Serdes.PLAYER_STATE_SERDE;

        SortedBag.Builder builder1 = new SortedBag.Builder();
        builder1.add(1, ChMap.tickets().get(3));
        builder1.add(8, ChMap.tickets().get(4));
        builder1.add(2, ChMap.tickets().get(6));
        builder1.add(8,ChMap.tickets().get(1));
        builder1.add(2, ChMap.tickets().get(7));
        builder1.add(3, ChMap.tickets().get(9));
        SortedBag<Ticket> tickets = builder1.build();

        SortedBag.Builder builder2 = new SortedBag.Builder();
        builder2.add(1, Card.BLACK);
        builder2.add(8, Card.LOCOMOTIVE);
        builder2.add(2, Card.BLUE);
        builder2.add(3, Card.GREEN);
        SortedBag<Card> cards = builder2.build();
        //         1,1,1,1,25,25,25,25,25,25,25,25,26,26,26,26,26,26,26,26,29,29,33,33;0,2,2,3,3,3,8,8,8,8,8,8,8,8;47,11,32,32,21

        List<Route> routes = List.of(ChMap.routes().get(3), ChMap.routes().get(23), ChMap.routes().get(9), ChMap.routes().get(8), ChMap.routes().get(6));

        PlayerState playerState = new PlayerState(tickets, cards, routes);

        String ser = i.serialize(playerState);
        PlayerState deser = i.deserialize(ser);

        assertEquals("1,1,1,1,25,25,25,25,25,25,25,25,26,26,26,26,26,26,26,26,29,29,33,33;" +
                "0,2,2,3,3,3,8,8,8,8,8,8,8,8;" +
                "47,11,32,32,21", ser);


        int a = 0;
        for (Ticket d : deser.tickets()) {
            assertEquals(tickets.get(a), d);
            a++;
        }

        int b = 0;
        for (Card d : deser.cards()) {
            assertEquals(cards.get(b), d);
            b++;
        }

        int c = 0;
        for (Route d : deser.routes()) {
            assertEquals(routes.get(c), d);
            c++;
        }
    }



}
