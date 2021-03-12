package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    @Test
    void topCardTest() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(25));
        assertEquals(Card.LOCOMOTIVE, deck.topCard());
    }

    @Test
    void withoutTopCardTest() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(0));
        deck = deck.withoutTopCard();
        assertEquals("[RED, BLUE, GREEN, LOCOMOTIVE, YELLOW]", deck.toString());
    }

    @Test
    void size() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertEquals(6, deck.size());
    }

    @Test
    void empty() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertEquals(true, deck.isEmpty());
    }

    @Test
    void topCardsFails() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(7);
        });
    }

    @Test
    void topCardFailEmpty() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCard();
        });
    }

    @Test
    void withoutTopCards() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(0));
        deck = deck.withoutTopCards(3);
        assertEquals("[RED, BLUE, GREEN]", deck.toString());
    }

    @Test
    void TopCard() {
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(0));
        assertEquals("{BLUE, YELLOW, LOCOMOTIVE}", deck.topCards(3).toString());

    }

    @Test
    void withOutFail(){
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCard();
        });

    }
    @Test
    void withOutsFail(){
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        SortedBag sortedBag = builder.add(2, Card.BLUE).add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.RED).add(Card.YELLOW).build();
        Deck deck = Deck.of(sortedBag, new Random(22));
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(7);
        });

    }
}
