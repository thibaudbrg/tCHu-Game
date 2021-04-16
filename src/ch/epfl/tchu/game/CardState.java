package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


/**
 * Represents the state of the wagon/locomotive cards that are not in the players' hands.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    /**
     * Private constructor
     *
     * @param faceUpCards  (List<Card>) The list of the 5 visible cards
     * @param deckSize     (int) The size of the deck
     * @param discardsSize (int) The size of the discard
     * @param deck         (Deck<Card>) The deck
     * @param discard      (SortedBag<Card>) The discard
     */
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deckSize, discardsSize);
        this.discard = discard;
        this.deck = deck;
    }

    /**
     * Calculates a state in which the 5 cards placed face up are the first 5 in the given pile.
     *
     * @param deck (Deck<Card>) The deck
     * @return A state in which the 5 cards placed face up are the first 5 in the given pile.
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        Deck<Card> deck1 = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);
        SortedBag<Card> discard1 = SortedBag.of();
        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), deck1.size(), discard1.size(), deck1, discard1);
    }

    /**
     * Give an identical set of cards to the receiver (this) but
     * the face-up index slot card has been replaced by the card at the top of the drawer, which is itself removed
     *
     * @param slot (int) The index of the concerned card
     * @return (CardState) An identical set of cards to the receiver (this) but
     * the face-up index slot card has been replaced by the card at the top of the drawer, which is itself removed.
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!deck.isEmpty());


        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        Deck<Card> newDeck = deck.withoutTopCard();
        newFaceUpCards.set(slot, deck.topCard());
        return new CardState(newFaceUpCards, newDeck.size(), this.discardsSize(), newDeck, this.discard);
    }

    /**
     * Give the Card at the top of the Deck
     *
     * @return (Card) The Card the top of the the Deck
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * Give a set of cards identical to the receiver (this), but without the card at the top of the deck
     *
     * @return (CardState) A set of cards identical to the receiver (this), but without the card at the top of the deck
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        Deck<Card> newDeck = deck.withoutTopCard();
        return new CardState(this.faceUpCards(), newDeck.size(), this.discardsSize(), newDeck, this.discard);
    }

    /**
     * Give an identical set of cards to the receiver (this), except that the cards in the discard pile have been shuffled
     * using the given random generator to make up the new draw pile.
     *
     * @param rng (Random) the random variable
     * @return (CardState) An identical set of cards to the receiver (this), except that the cards in the discard
     * pile have been shuffled using the given random generator to make up the new draw pile.
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        Deck<Card> newDeck = Deck.of(discard, rng);
        SortedBag<Card> newDiscard = SortedBag.of();
        return new CardState(this.faceUpCards(), newDeck.size(), newDiscard.size(), newDeck, newDiscard);
    }

    /**
     * Give a set of cards identical to the receiver (this), but with the given cards added to the discard pile.
     *
     * @param additionalDiscards (SortedBag<Card>) The additional cards.
     * @return (CardState) a set of cards identical to the receiver (this), but with the given cards added to the discard pile.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> newDiscard = this.discard.union(additionalDiscards);
        return new CardState(this.faceUpCards(), this.deckSize(), newDiscard.size(), this.deck, newDiscard);
    }

}
