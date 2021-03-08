package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicCardStateTest {

    @Test
    void failsWithNegative() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(d, 6, 0);
        });
    }

    @Test
    void failsWithNegative1() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(d, -3, 0);
        });
    }

    @Test
    void failsWithNegative2() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(d, 5, -1);
        });
    }
    @Test
    void totalSizeTest() {
        List<Card> d = List.of(Card.BLUE,Card.ORANGE,Card.RED,Card.WHITE,Card.LOCOMOTIVE);
      PublicCardState s = new PublicCardState(d,8,3);
      assertEquals(5+8+3,s.totalSize());
    }

    @Test
    void faceUpCards() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 8, 3);
        assertEquals(d, s.faceUpCards());
    }
    @Test
    void faceUpCard() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 8, 3);
        assertEquals(d.get(3), s.faceUpCard(3));
    }
    @Test
    void faceUpCardFails() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 8, 3);
        assertThrows(IndexOutOfBoundsException.class,()->{s.faceUpCard(5);});
    }
    @Test
    void deckSize() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 8, 3);
        assertEquals(8,s.deckSize());
    }
    @Test
    void discardSize() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 8, 3);
        assertEquals(3,s.discardsSize());
    }
    @Test
    void isEmpty() {
        List<Card> d = List.of(Card.BLUE, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE);
        PublicCardState s = new PublicCardState(d, 5, 3);
       assertFalse(s.isDeckEmpty());
    }
}

