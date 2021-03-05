package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import jdk.nashorn.api.tree.DebuggerTree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class CardState extends PublicCardState {

    private final Deck<Card> pioche;
    private final SortedBag<Card> defausse;

    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> pioche, SortedBag<Card> defausse) {
        super(faceUpCards, deckSize, discardsSize);
        this.defausse = defausse;
        this.pioche = pioche;
    }

    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        Deck<Card> pioche1 = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);
        SortedBag<Card> defausse2 = SortedBag.of();
        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), pioche1.size(), 0, pioche1, defausse2);
    }

    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!pioche.isEmpty());
        Objects.checkIndex(0, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = this.faceUpCards();
        Deck<Card> newPioche = pioche.withoutTopCards(0);
        newFaceUpCards.remove(slot);
        newFaceUpCards.add(newPioche.topCard());
        newPioche.withoutTopCard();

        return new CardState(newFaceUpCards, newPioche.size(), this.discardsSize(), newPioche, this.defausse);
    }

    public Card topDeckCard() {
        Preconditions.checkArgument(!pioche.isEmpty());
        return pioche.topCard();
    }

    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!pioche.isEmpty());
        return new CardState(this.faceUpCards(), pioche.size() - 1, this.discardsSize(), pioche.withoutTopCard(), this.defausse);
    }


    public CardState withDeckRecreateFromDiscards(Random rng) {
        Preconditions.checkArgument(pioche.isEmpty());
        List<Card> newDefausse = this.defausse.toList();
        Collections.shuffle(newDefausse, rng);


        return new CardState(this.faceUpCards(), this.deckSize(), this.discardsSize(), this.pioche, SortedBag.of(newDefausse));
    }

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        //TODO VERIFIER POUR L'UNION
        return new CardState(this.faceUpCards(), this.deckSize(), this.discardsSize(), this.pioche, this.defausse.union(additionalDiscards));
    }
}
