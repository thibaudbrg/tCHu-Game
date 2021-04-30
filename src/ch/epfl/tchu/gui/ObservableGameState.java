package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static javafx.collections.FXCollections.*;

public final class ObservableGameState {

    private PlayerId playerId;

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

    //TODO LE PROF DECRIT UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT,
    //TODO ALORS COMMENT FAIRE POUR FAIRE UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT MAIS QUI PREND EN ARGUMENTS UN ID
    public ObservableGameState(PlayerId id) {
        this.playerId = id;
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


    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        // refresh the percentTicketsRemainingInDeck
        percentTicketsRemainingInDeck.set((int) Math.floor(((double) newGameState.ticketsCount() / Constants.TICKETS_COUNT) * 100d));

        // refresh the percentCardsRemainingInDeck
        percentCardsRemainingInDeck.set((int) Math.floor(((double) newGameState.cardState().deckSize() / Constants.ALL_CARDS.size()) * 100d));

        // refresh the faceUpCard
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        // refresh the routes
        int i = 0;
        routes.forEach((r, id) -> {
            if (newGameState.claimedRoutes().contains(r)) {
                id.setValue(newPlayerState.routes().contains(r) ? newGameState.currentPlayerId() : newGameState.currentPlayerId().next()); //TODO PAS SUR DES PLAYERID
            }
        });

        // refresh the numberOfTicketsOnHand
        numberOfTicketsOnHand.get(newGameState.currentPlayerId()).set(newPlayerState.ticketCount());
        numberOfTicketsOnHand.get(newGameState.currentPlayerId().next()).set(newGameState.playerState(playerId.next()).ticketCount());

        // refresh the numberOfCardsOnHand
        numberOfCardsOnHand.get(newGameState.currentPlayerId()).set(newPlayerState.cardCount());
        numberOfCardsOnHand.get(newGameState.currentPlayerId()).set(newGameState.playerState(playerId.next()).cardCount());

        // refresh the numberOfCarsOnHand
        numberOfCarsOnHand.get(newGameState.currentPlayerId()).set(newPlayerState.carCount());
        numberOfCarsOnHand.get(newGameState.currentPlayerId()).set(newGameState.playerState(playerId.next()).carCount());

        // refresh the numberOfBuildingPointsOnHand
        numberOfBuildingPointsOnHand.get(newGameState.currentPlayerId()).set(newPlayerState.claimPoints());
        numberOfBuildingPointsOnHand.get(newGameState.currentPlayerId()).set(newGameState.playerState(playerId.next()).claimPoints());

        // refresh the ticketsOnHand
       System.out.println(newPlayerState.tickets().size());
       ticketsOnHand.setAll(newPlayerState.tickets().toList());



        // refresh the numberOfEachCards
        numberOfEachCards.forEach((card,integerProperty)->{
            integerProperty.setValue(newPlayerState.cards().countOf(card));
        });

        // refresh the claimForEachRoute
        claimForEachRoute.forEach((r, b) -> {
            if (newGameState.currentPlayerId().equals(playerId)) {
                if (!newGameState.claimedRoutes().contains(r)) {
                    List<List<Station>> listClaimedRouteStation = new LinkedList<>();
                    for (Route route : newGameState.claimedRoutes()) {
                        listClaimedRouteStation.add(route.stations());

                    }
                    if (!listClaimedRouteStation.contains(r.stations())) {
                        if (newPlayerState.canClaimRoute(r)) {
                            b.setValue(true);
                        }
                    } else b.setValue(false);

                } else b.setValue(false);
            } else b.setValue(false);
        });


    }

    //==============================================================//


    public ReadOnlyIntegerProperty percentTicketsRemainingInDeckProperty() {
        return percentTicketsRemainingInDeck;
    }

    public final int getPercentTicketsRemainingInDeck() {
        return percentTicketsRemainingInDeck.get();
    }


    public final ReadOnlyIntegerProperty percentCardsRemainingInDeckProperty() {
        return percentCardsRemainingInDeck;
    }

    public final int getPercentCardsRemainingInDeck() {
        return percentCardsRemainingInDeck.get();
    }


    public final ReadOnlyObjectProperty<Card> faceUpCardsProperty(int slot) {
        return faceUpCards.get(slot);
    }

    public final Card getFaceUpCard(int slot) {
        return faceUpCards.get(slot).get();
    }

    private final static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new SimpleObjectProperty<>());
        }
        return list;
    }


    public final ReadOnlyObjectProperty<PlayerId> routesProperty(Route route) {
        return routes.get(route);
    }

    public final PlayerId getRoutes(Route route) {
        return routes.get(route).get();
    }

    private final static Map<Route, ObjectProperty<PlayerId>> createRoute() {
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            map.put(route, new SimpleObjectProperty<>());
        }
        return Collections.unmodifiableMap(map);
    }


    //==============================================================//


    public final ReadOnlyIntegerProperty numberOfTicketsOnHandProperty(PlayerId playerId) {
        return numberOfTicketsOnHand.get(playerId);
    }

    public final int getNumberOfTicketsOnHand(PlayerId playerId) {
        return numberOfTicketsOnHandProperty(playerId).get();
    }


    public final ReadOnlyIntegerProperty numberOfCardsOnHandProperty(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId);
    }

    public final int getNumberOfCardsOnHand(PlayerId playerId) {
        return numberOfCardsOnHand.get(playerId).get();
    }


    public final ReadOnlyIntegerProperty numberOfCarsOnHandProperty(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId);
    }

    public final int getNumberOfCarsOnHand(PlayerId playerId) {
        return numberOfCarsOnHand.get(playerId).get();
    }


    public final ReadOnlyIntegerProperty numberOfBuildingPointsOnHandProperty(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId);
    }

    public final int getNumberOfBuildingPointsOnHand(PlayerId playerId) {
        return numberOfBuildingPointsOnHand.get(playerId).get();
    }


    private final static Map<PlayerId, IntegerProperty> createMapIntPropertyBothPlayers() {
        Map<PlayerId, IntegerProperty> map = new HashMap<>();
        map.put(PlayerId.PLAYER_1, new SimpleIntegerProperty());
        map.put(PlayerId.PLAYER_2, new SimpleIntegerProperty());
        return Collections.unmodifiableMap(map);
    }


    //==============================================================//


    public final ObservableList<Ticket> ticketsOnHandProperty() {
        return unmodifiableObservableList(ticketsOnHand);
    }

    public final Ticket getTicketOnHand(int slot) {
        return ticketsOnHand.get(slot);
    }


    public final ReadOnlyIntegerProperty numberOfEachCardsProperty(Card card) {
        return numberOfEachCards.get(card);
    }

    public final int getNumberOfEachCards(Card card) {
        return numberOfEachCards.get(card).get();
    }

    private final static Map<Card, IntegerProperty> createNumberOfEachCard() {
        Map<Card, IntegerProperty> map = new HashMap<>();
        for (Card card : Card.ALL) {
            map.put(card, new SimpleIntegerProperty());
        }
        return Collections.unmodifiableMap(map);
    }


    public final ReadOnlyBooleanProperty claimForEachRouteProperty(Route route) {
        return claimForEachRoute.get(route);
    }

    public final boolean getClaimForEachRoute(Route route) {
        return claimForEachRoute.get(route).get();
    }

    private final static Map<Route, BooleanProperty> createClaimForEachRoute() {
        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            map.put(route, new SimpleBooleanProperty());
        }
        return Collections.unmodifiableMap(map);
    }


    //==============================================================//


    //TODO Coder les dernières méthodes

    private final void canDrawTickets(PublicGameState publicGameState) {
        publicGameState.canDrawTickets();
    }

    private final void canDrawCards(PublicGameState publicGameState) {
        publicGameState.canDrawCards();
    }

    private final void possibleClaimCards(PlayerState playerState, Route route) {
        playerState.possibleClaimCards(route);

    }

}
