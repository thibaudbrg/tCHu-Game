package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;


    private Deck(List<C> cards) {
        this.cards = cards;
    }

    public static <C extends Comparable<C>> Deck<C> of(List<C> cards, Random rng) {
        Collections.shuffle(cards, rng);
        return new Deck<C>(cards);

    }


    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public C topCard() {
        Preconditions.checkArgument(!this.isEmpty());
        return cards.get(size() - 1);
    }

    public Deck<C> withoutTopCard() {
        return new Deck(this.cards.subList(1, cards.lastIndexOf(this)));
    }

    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        SortedBag.Builder<C> builder= new SortedBag.Builder<>();
        for (int i = 0; i < count; i++) {
            builder.add(cards.get(size() - i));
        }
        return builder.build();
    }

    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return new Deck<C>(this.cards.subList(0, size() - count));
    }
}
