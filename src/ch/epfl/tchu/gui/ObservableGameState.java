package ch.epfl.tchu.gui;

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
    private final Map<Ticket, BooleanProperty> ticketsComplete;
    private final Map<Card, IntegerProperty> numberOfEachCards;
    private final Map<Route, BooleanProperty> claimForEachRoute;
    private final Map<Route, BooleanProperty> longestTrail;
    private final BooleanProperty update;

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
update = new SimpleBooleanProperty();
        numberOfTicketsOnHand = createMapIntPropertyBothPlayers();
        numberOfCardsOnHand = createMapIntPropertyBothPlayers();
        numberOfCarsOnHand = createMapIntPropertyBothPlayers();
        numberOfBuildingPointsOnHand = createMapIntPropertyBothPlayers();

        ticketsOnHand = FXCollections.observableArrayList();
        ticketsComplete = new HashMap<>();

        numberOfEachCards = createNumberOfEachCard();
        claimForEachRoute = createClaimForEachRoute();
        longestTrail = createClaimForEachRoute();
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
        update.set(numberOfCardsOnHand.get(playerId).get() == newGameState.playerState(playerId).cardCount());
        percentTicketsRemainingInDeck.set((int) Math.floor(((double) newGameState.ticketsCount() / (double) Constants.TICKETS_COUNT) * 100));
        percentCardsRemainingInDeck.set((int) Math.floor(((double) newGameState.cardState().deckSize() / (double) Constants.ALL_CARDS.size()) * 100));

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        routes.forEach((r, id) -> {
            if (id.get() == null) {
                if (newGameState.claimedRoutes().contains(r)) {
                    id.setValue(newPlayerState.routes().contains(r) ? playerId : playerId.next());
                }
            }
        });
        List<Route> longestTCurrentPlayer = Trail.longest(newPlayerState.routes()).getRoutes();
        List<Route> longestTOtherPlayer = Trail.longest(newGameState.playerState(playerId.next()).routes()).getRoutes();
        List<Route> longestRoute = Trail.trailLength(longestTCurrentPlayer) < Trail.trailLength(longestTOtherPlayer) ? longestTOtherPlayer : longestTCurrentPlayer;
        if (Trail.trailLength(longestTCurrentPlayer) == Trail.trailLength(longestTOtherPlayer)) {
            longestRoute.addAll(longestTOtherPlayer);
        }
        longestTrail.forEach((r, b) -> {
            if (longestRoute.contains(r)) b.set(true);
            else b.set(false);
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

        for (Ticket t : ticketsOnHand) {
            ticketsComplete.putIfAbsent(t, new SimpleBooleanProperty());
            BooleanProperty propertyTickCom = ticketsComplete.get(t);
            if (!propertyTickCom.get()) {
                propertyTickCom.set(newPlayerState.ticketsDone(t));
            }
        }

        numberOfEachCards.forEach((card, integerProperty) -> integerProperty.setValue(newPlayerState.cards().countOf(card)));

        claimForEachRoute.forEach((r, b) -> {
            if (newGameState.currentPlayerId().equals(playerId)) {
                if (!newGameState.claimedRoutes().contains(r)) {
                    if (newGameState.claimedRoutes().stream().map(Route::stations).noneMatch(route -> route.equals(r.stations()))) {
                        if (newPlayerState.canClaimRoute(r)) {
                            b.setValue(true);
                        }
                    } else b.setValue(false);
                } else b.setValue(false);
            } else b.setValue(false);
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
    public int getPercentTicketsRemainingInDeck() {
        return percentTicketsRemainingInDeck.get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property
     *
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty percentCardsRemainingInDeckProperty() {
        return percentCardsRemainingInDeck;
    }

    /**
     * Returns the value of the property
     *
     * @return (int) The value
     */
    public int getPercentCardsRemainingInDeck() {
        return percentCardsRemainingInDeck.get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property at the chosen index
     *
     * @param slot (int) the chosen index
     * @return (ReadOnlyObjectProperty < Card >) the unmodifiable property
     */
    public ReadOnlyObjectProperty<Card> faceUpCardsProperty(int slot) {
        return faceUpCards.get(slot);
    }


    /**
     * Returns the value of the property at the chosen index
     *
     * @param slot (int) The index
     * @return (Card) The value
     */
    public Card getFaceUpCard(int slot) {
        return faceUpCards.get(slot).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property at the chosen route
     *
     * @param route (Route) the chosen route
     * @return (ReadOnlyObjectProperty < PlayerId >) the unmodifiable property
     */
    public ReadOnlyObjectProperty<PlayerId> routesProperty(Route route) {
        return routes.get(route);
    }

    /**
     * Returns the value of the property at the chosen route
     *
     * @param route (Route) The route
     * @return (PlayerId) The value
     */
    public PlayerId getRoutes(Route route) {
        return routes.get(route).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty numberOfTicketsOnHandProperty(PlayerId playerId) {
        return numberOfTicketsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public int getNumberOfTicketsOnHand(PlayerId playerId) {
        return numberOfTicketsOnHandProperty(playerId).get();
    }

    //==============================================================//


    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty numberOfCardsOnHandProperty(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public int getNumberOfCardsOnHand(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty numberOfCarsOnHandProperty(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public int getNumberOfCarsOnHand(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen PlayerId
     *
     * @param playerId (playerId) the chosen playerId
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty numberOfBuildingPointsOnHandProperty(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId);
    }

    /**
     * Returns the value of the property at the chosen playerId
     *
     * @param playerId (PlayerId) The playerId
     * @return (int) The value
     */
    public int getNumberOfBuildingPointsOnHand(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property
     *
     * @return (ObservableList < Ticket >) the unmodifiable property
     */
    public ObservableList<Ticket> ticketsOnHandProperty() {
        return unmodifiableObservableList(ticketsOnHand);
    }

    /**
     * Returns the value of the property at the chosen index
     *
     * @param slot (int) The index
     * @return (Ticket) The value
     */
    public Ticket getTicketOnHand(int slot) {
        return ticketsOnHand.get(slot);
    }


    /**
     * Returns the corresponding property for the chosen ticket
     *
     * @param t (Ticket) The given ticket
     * @return (ReadOnlyIntegerProperty) The unmodifiable property
     */

    public ReadOnlyBooleanProperty ticketCompleteProperty(Ticket t) {
        return ticketsComplete.get(t);
    }

    /**
     * Returns the value of the property at the chosen Ticket
     *
     * @param t (Ticket) The ticket
     * @return (boolean) the value
     */

    public boolean getTicketComplete(Ticket t) {
        return ticketsComplete.get(t).get();

    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen Card
     *
     * @param card (Card) the chosen card
     * @return (ReadOnlyIntegerProperty) the unmodifiable property
     */
    public ReadOnlyIntegerProperty numberOfEachCardsProperty(Card card) {
        return numberOfEachCards.get(card);
    }

    /**
     * Returns the value of the property at the chosen card
     *
     * @param card (Card) The card
     * @return (int) The value
     */
    public int getNumberOfEachCards(Card card) {
        return numberOfEachCards.get(card).get();
    }

    //==============================================================//

    /**
     * Returns the corresponding property for the chosen Route
     *
     * @param route (Route) The chosen route
     * @return (ReadOnlyBooleanProperty) The unmodifiable property
     */
    public ReadOnlyBooleanProperty claimForEachRouteProperty(Route route) {
        return claimForEachRoute.get(route);
    }

    /**
     * Returns the value of the property at the chosen Route
     *
     * @param route (Route) The route
     * @return (boolean) The value
     */
    public boolean getClaimForEachRoute(Route route) {
        return claimForEachRoute.get(route).get();
    }

    //==============================================================//
    //==============================================================//

    /**
     * Call gameState.canDrawTickets
     *
     * @return (boolean) True if the tickets deck isn't empty
     */
    public boolean canDrawTickets() {

        return gameState.canDrawTickets();
    }

    /**
     * Call gameState.canDrawCards
     *
     * @return (boolean) True iff the deck and the discards contains at least 5 cards
     */
    public boolean canDrawCards() {

        return gameState.canDrawCards();
    }

    /**
     * Call gameState.possibleClaimCards
     *
     * @param route (Route) The given Route
     * @return (List < SortedBag < Card > >) A list of all the sets of cards that the player could use to take possession of the given route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);

    }

    public ReadOnlyBooleanProperty longuestTrailProperty(Route r) {
        return longestTrail.get(r);
    }

    public boolean longuestTrail(Route r) {
        return longestTrail.get(r).get();

    }
    public boolean numberOfCardChanged(){
        return update.get();
    }


    //==============================================================//
    //==============================================================//

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
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
