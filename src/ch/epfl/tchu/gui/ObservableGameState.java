package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * Represents the observable state of a game of tCHu. Includes the public part of the game state,
 * i.e. the information contained in an instance of PublicGameState
 * and the entire state of a given player, i.e. the information contained in an instance of PlayerState
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class ObservableGameState {

    private final PlayerId playerId;
    private PublicGameState gameState;
    private PlayerState playerState;

    // Properties concerning the public state of the game
    private final IntegerProperty percentTicketsRemainingInDeck;
    private final IntegerProperty percentCardsRemainingInDeck;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routes;

    // Properties concerning the public state for each player
    private final Map<PlayerId, IntegerProperty> numberOfTicketsOnHand;
    private final Map<PlayerId, IntegerProperty> numberOfCardsOnHand;
    private final Map<PlayerId, IntegerProperty> numberOfCarsOnHand;
    private final Map<PlayerId, IntegerProperty> numberOfBuildingPointsOnHand;

    //Properties concerning the private state of the player who instantiates ObservableGameState
    private final ObservableList<Ticket> ticketsOnHand;
    private final Map<Card, IntegerProperty> numberOfEachCards;
    private final Map<Route, BooleanProperty> claimForEachRoute;

    /**
     * Takes as argument the identity of the player to which it corresponds
     *
     * @param id (PlayerId) The identity of the player
     */
    public ObservableGameState(PlayerId id) {
        this.playerId = Objects.requireNonNull(id);
        percentTicketsRemainingInDeck = new SimpleIntegerProperty();
        percentCardsRemainingInDeck = new SimpleIntegerProperty();
        faceUpCards = createFaceUpCards();
        routes = createRoute();

        numberOfTicketsOnHand = createMapIntPropertyBothPlayers();
        numberOfCardsOnHand = createMapIntPropertyBothPlayers();
        numberOfCarsOnHand = createMapIntPropertyBothPlayers();
        numberOfBuildingPointsOnHand = createMapIntPropertyBothPlayers();

        ticketsOnHand = FXCollections.observableArrayList();
        numberOfEachCards = createNumberOfEachCard();
        claimForEachRoute = createClaimForEachRoute();
    }


    /**
     * Updates the state it contains (the set of properties),
     * taking as argument the public part of the game and the complete state of the player it corresponds to
     *
     * @param newGameState   (PublicGameState) The new PublicGameState
     * @param newPlayerState (PlayerState) The new PlayerState
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        gameState = Objects.requireNonNull(newGameState);
        playerState = Objects.requireNonNull(newPlayerState);

        percentTicketsRemainingInDeck.set((int) Math.floor(((double) newGameState.ticketsCount() / Constants.TICKETS_COUNT) * 100d));
        percentCardsRemainingInDeck.set((int) Math.floor(((double) newGameState.cardState().deckSize() / Constants.ALL_CARDS.size()) * 100d));

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        routes.forEach((r, id) -> {
            if (id == null) {
                if (newGameState.claimedRoutes().contains(r)) {
                    id.setValue(newPlayerState.routes().contains(r) ? playerId : playerId.next());
                }
            }
        });

        numberOfTicketsOnHand.get(playerId).set(newPlayerState.ticketCount());
        numberOfTicketsOnHand.get(playerId.next()).set(newGameState.playerState(playerId.next()).ticketCount());

        numberOfCardsOnHand.get(playerId).set(newPlayerState.cardCount());
        numberOfCardsOnHand.get(playerId.next()).set(newGameState.playerState(playerId.next()).cardCount());

        numberOfCarsOnHand.get(playerId).set(newPlayerState.carCount());
        numberOfCarsOnHand.get(playerId.next()).set(newGameState.playerState(playerId.next()).carCount());

        numberOfBuildingPointsOnHand.get(playerId).set(newPlayerState.claimPoints());
        numberOfBuildingPointsOnHand.get(playerId.next()).set(newGameState.playerState(playerId.next()).claimPoints());

        ticketsOnHand.setAll(newPlayerState.tickets().toList());

        numberOfEachCards.forEach((card, integerProperty) -> integerProperty.setValue(newPlayerState.cards().countOf(card)));

        claimForEachRoute.forEach((r, b) -> {
            if (b == null) {
                if (newGameState.currentPlayerId().equals(playerId)) {
                    if (!newGameState.claimedRoutes().contains(r)) {
                        if (newGameState.claimedRoutes().stream()
                                .map(Route::stations)
                                .noneMatch((listStations) -> listStations.contains(r.stations()))) {
                            if (newPlayerState.canClaimRoute(r)) {
                                b.setValue(true);
                            }
                        } else b.setValue(false);
                    } else b.setValue(false);
                } else b.setValue(false);
            }
        });


    }

    //==============================================================//
    //==============================================================//

    /**
     * Returns the corresponding property
     *
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty percentTicketsRemainingInDeckProperty() {
        return percentTicketsRemainingInDeck;
    }

    /**
     * Returns the value of the property
     *
     * @return (int) The value
     */
    public final int getPercentTicketsRemainingInDeck() {
        return percentTicketsRemainingInDeck.get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property
     *
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty percentCardsRemainingInDeckProperty() {
        return percentCardsRemainingInDeck;
    }

    /**
     * Returns the value of the property
     *
     * @return (int) The value
     */
    public final int getPercentCardsRemainingInDeck() {
        return percentCardsRemainingInDeck.get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property at the chosen index
     *
     * @param slot (int) the chosen index
     * @return (ReadOnlyObjectProperty < Card >) the unmodifiable property
     */
    public final ReadOnlyObjectProperty<Card> faceUpCardsProperty(int slot) {
        return faceUpCards.get(slot);
    }


    /**
     * Returns the value of the property at the chosen index
     *
     * @param slot (int) The index
     * @return (Card) The value
     */
    public final Card getFaceUpCard(int slot) {
        return faceUpCards.get(slot).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property at the chosen route
     *
     * @param route (Route) the chosen route
     * @return (ReadOnlyObjectProperty < PlayerId >) the unmodifiable property
     */
    public final ReadOnlyObjectProperty<PlayerId> routesProperty(Route route) {
        return routes.get(route);
    }

    /**
     * Returns the value of the property at the chosen route
     *
     * @param route (Route) The route
     * @return (PlayerId) The value
     */
    public final PlayerId getRoutes(Route route) {
        return routes.get(route).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty numberOfTicketsOnHandProperty(PlayerId playerId) {
        return numberOfTicketsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public final int getNumberOfTicketsOnHand(PlayerId playerId) {
        return numberOfTicketsOnHandProperty(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty numberOfCardsOnHandProperty(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public final int getNumberOfCardsOnHand(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty numberOfCarsOnHandProperty(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public final int getNumberOfCarsOnHand(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty numberOfBuildingPointsOnHandProperty(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public final int getNumberOfBuildingPointsOnHand(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property
     *
     * @return (ObservableList < Ticket >) the unmodifiable property
     */
    public final ObservableList<Ticket> ticketsOnHandProperty() {
        return unmodifiableObservableList(ticketsOnHand);
    }

    /**
     * Returns the value of the property at the chosen index
     *
     * @param slot (int) The index
     * @return (Ticket) The value
     */
    public final Ticket getTicketOnHand(int slot) {
        return ticketsOnHand.get(slot);
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen Card
     *
     * @param card (Card) the chosen card
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public final ReadOnlyIntegerProperty numberOfEachCardsProperty(Card card) {
        return numberOfEachCards.get(card);
    }

    /**
     * Returns the value of the property at the chosen card
     *
     * @param card (Card) The card
     * @return (int) The value
     */
    public final int getNumberOfEachCards(Card card) {
        return numberOfEachCards.get(card).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen Route
     *
     * @param route (Route) the chosen route
     * @return (ReadOnlyBooleanProperty) the unmodifiable property
     */
    public final ReadOnlyBooleanProperty claimForEachRouteProperty(Route route) {
        return claimForEachRoute.get(route);
    }

    /**
     * Returns the value of the property at the chosen Route
     *
     * @param route (Route) The route
     * @return (boolean) The value
     */
    public final boolean getClaimForEachRoute(Route route) {
        return claimForEachRoute.get(route).get();
    }

    //==============================================================//
    //==============================================================//

    /**
     * Call gameState.canDrawTickets
     *
     * @return (boolean) true if the tickets deck isn't empty
     */
    public final boolean canDrawTickets() {
        Preconditions.checkArgument(gameState != null);
        return gameState.canDrawTickets();
    }

    /**
     * Call gameState.canDrawCards
     *
     * @return (boolean) true iff the deck and the discards contains at least 5 cards
     */
    public final boolean canDrawCards() {
        Preconditions.checkArgument(gameState != null);
        return gameState.canDrawCards();
    }

    /**
     * Call gameState.possibleClaimCards
     *
     * @param route (Route) The given Route
     * @return (List < SortedBag < Card > >) A list of all the sets of cards that the player could use to take possession of the given route
     */
    public final List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(playerState != null);
        return playerState.possibleClaimCards(route);

    }

    //==============================================================//
    //==============================================================//

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new SimpleObjectProperty<>());
        }
        return Collections.unmodifiableList(list);
    }

    private static Map<Route, ObjectProperty<PlayerId>> createRoute() {
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            map.put(route, new SimpleObjectProperty<>());
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<PlayerId, IntegerProperty> createMapIntPropertyBothPlayers() {
        Map<PlayerId, IntegerProperty> map = new HashMap<>();
        map.put(PlayerId.PLAYER_1, new SimpleIntegerProperty());
        map.put(PlayerId.PLAYER_2, new SimpleIntegerProperty());
        return Collections.unmodifiableMap(map);
    }

    private static Map<Card, IntegerProperty> createNumberOfEachCard() {
        Map<Card, IntegerProperty> map = new HashMap<>();
        for (Card card : Card.ALL) {
            map.put(card, new SimpleIntegerProperty());
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Route, BooleanProperty> createClaimForEachRoute() {
        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            map.put(route, new SimpleBooleanProperty());
        }
        return Collections.unmodifiableMap(map);
    }
}
