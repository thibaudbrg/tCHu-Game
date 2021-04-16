package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

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
    void EnumTypeSerdeTest() {
        Serde<Card> i = Serdes.CARD_SERDE;
        List<String> ser = new ArrayList<>();
        int a = 0;
        for (Card c : Card.ALL) {
            ser.add(i.serialize(c));
            assertEquals(String.valueOf(a), i.serialize(c));
            a++;

        }
        int b = 0;
        for (String s : ser) {
            assertEquals(Card.ALL.get(b), i.deserialize(s));
            b++;
        }


    }

    @Test
    void SerdeListTest() {

    }
}