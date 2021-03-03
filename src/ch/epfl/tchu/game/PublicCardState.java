package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;


public class PublicCardState {
    List<Card> faceUpCards;
    private int deckSize;
    private int discardsSize;

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        Preconditions.checkArgument(faceUpCards.size() == 5);
        Preconditions.checkArgument(deckSize > 0);
        Preconditions.checkArgument(discardsSize > 0);
    }

    public int totalSize() {
        return faceUpCards.size() + deckSize + discardsSize;

    }

    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    public Card faceUpCard(int slot) {
        Objects.checkIndex(0, faceUpCards.size());
        return faceUpCards.get(slot);
    }

    public int deckSize() {
        return deckSize;
    }

    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    public int discardsSize() {
        return discardsSize;
    }
}
