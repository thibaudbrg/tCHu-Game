package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;


import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {


    @Test
    void ofFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(Deck.of(SortedBag.of(Card.ORANGE), new Random(23)));
        });
    }

    @Test
    void withDrawnFailsWithBadIndex() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(5, Card.YELLOW).add(10, Card.ORANGE).add(3, Card.RED);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            v1.withDrawnFaceUpCard(5);

        });
    }

    @Test
    void withDrawnFailsWithBadDeck() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(5, Card.YELLOW);
        CardState v1 = CardState.of(Deck.of(builder.build(), new Random(20)));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            v1.withDrawnFaceUpCard(3);

        });
    }

    /*
   @Test
    void withDrawnFaceUpCardWorks() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(5, Card.YELLOW).add(4, Card.RED).add(6, Card.ORANGE).add(3, Card.LOCOMOTIVE);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);
        v1.
                v1.withDrawnFaceUpCard(3);
        assertEquals([]);


    });}
    */
}

