package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;


import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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

        assertThrows(IllegalArgumentException.class, () -> {
            v1.withDrawnFaceUpCard(3);

        });
    }


    @Test
    void withDrawnFaceUpCardWorks() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(3, Card.YELLOW).add(2, Card.RED).add(2, Card.ORANGE).add(2, Card.LOCOMOTIVE);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);
        CardState v2 = v1.withDrawnFaceUpCard(3);

        assertEquals("[YELLOW, YELLOW, ORANGE, YELLOW, LOCOMOTIVE]", v2.faceUpCards());
    }

    @Test
    void deckRecreatedFailsWithDeckNotEmpty() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(3, Card.YELLOW).add(2, Card.RED).add(2, Card.ORANGE).add(2, Card.LOCOMOTIVE);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            v1.withDeckRecreatedFromDiscards(new Random(2));
        });

    }


    @Test
    void withDeckRecreatedFromDiscardsWorks() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(3, Card.YELLOW).add(2, Card.RED).add(2, Card.ORANGE).add(2, Card.LOCOMOTIVE);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);
        while (v1.deckSize() > 0) {
           v1 = v1.withDrawnFaceUpCard(1);

        }
        SortedBag.Builder<Card> bder = new SortedBag.Builder();
        SortedBag sortedBag = bder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
       CardState v2 = v1.withMoreDiscardedCards(sortedBag);
       v2= v2.withDeckRecreatedFromDiscards(new Random(12));
assertEquals(6,v2.deckSize());
        assertEquals(v1.faceUpCards(),v2.faceUpCards());



    }

    @Test
    void withMoreDiscardedCardsWorks() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(3, Card.YELLOW).add(2, Card.RED).add(2, Card.ORANGE).add(2, Card.LOCOMOTIVE);
        Deck<Card> deck = Deck.of(builder.build(), new Random(20));
        CardState v1 = CardState.of(deck);

        SortedBag.Builder<Card> builder1 = new SortedBag.Builder();
        builder1.add(2, Card.GREEN).add(Card.BLACK);
        builder1.build();

        assertEquals(3, v1.discardsSize());
    }
}
