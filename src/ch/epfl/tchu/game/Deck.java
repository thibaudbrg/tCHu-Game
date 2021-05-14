package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Represents a deck of cards.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */

public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    /**
     * Private constructor
     *
     * @param cards (List<C>) List of cards that constitute the deck
     */
    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * Returns a shuffled deck of cards
     *
     * @param cards (SortedBag<C>) Input list of cards
     * @param rng   (Random) random coefficient
     * @param <C>   type stocked in the deck
     * @return (Deck < C >) a shuffled deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> shuffledCards = new ArrayList<>(cards.toList());
        Collections.shuffle(shuffledCards, rng);
        return new Deck<>(shuffledCards);

    }


    /**
     * Returns the top card of the deck
     *
     * @return (C) the top card of the deck
     */
    public C topCard() {
        Preconditions.checkArgument(!this.isEmpty());
        return cards.get(size() - 1);
    }

    /**
     * Returns a deck without the top card
     *
     * @return (Deck < C >) a deck without the top card
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return withoutTopCards(1);
    }

    /**
     * Returns the count top cards
     *
     * @param count (int) number of top cards that we want
     * @return (SortedBag < C >) the count top cards
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        List<C> s = cards.stream()
                .skip(cards.size() - count)
                .collect(Collectors.toList());

        return SortedBag.of(s);
    }

    /**
     * Returns a deck without the count top cards.
     *
     * @param count (int) number of top cards that we don't want in the new deck
     * @return (Deck < C >) a deck without the count top cards.
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return new Deck<>(this.cards.subList(0, size() - count));
    }

    /**
     * Returns the size of the deck
     *
     * @return (int) the size of the deck
     */
    public int size() {
        return cards.size();
    }


    /**
     * Returns true if the deck is empty
     *
     * @return (boolean) true if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

}