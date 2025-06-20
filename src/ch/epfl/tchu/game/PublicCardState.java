package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the "public" part of the status of all cards in the deck.
 * In other words, all the information that the player is supposed to know,
 * such as the number of cards in the deck, but does not know the contents of the deck.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Builds a public state of the cards in which the face up cards are the given ones,
     * the deck contains deckSize cards and the discard contains discardsSize cards.
     *
     * @param faceUpCards  (List<Card>) The list of cards face up
     * @param deckSize     (int) The number of cards on the deck
     * @param discardsSize (int) The number of cards on the discard
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0);
        Preconditions.checkArgument(discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;

    }


    /**
     * Give the 5 cards face up, in the form of a list with exactly 5 elements
     *
     * @return (List < Card >) the 5 cards face up, in the form of a list with exactly 5 elements
     */
    public List<Card> faceUpCards() {
        return Collections.unmodifiableList(faceUpCards);
    }

    /**
     * Give the card face up at the given index
     *
     * @param slot (int) The index of the Card
     * @return (Card) the card face up at the given index
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(0, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * Give the size the deck
     *
     * @return (int) the size the deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Return true iff the deck is empty
     *
     * @return (boolean) true iff the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Give the size of the discard
     *
     * @return (int) the size of the discard
     */
    public int discardsSize() {
        return discardsSize;
    }
}
