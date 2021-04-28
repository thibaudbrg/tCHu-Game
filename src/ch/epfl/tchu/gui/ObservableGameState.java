package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;

public final class ObservableGameState {

    private PlayerId playerId;

    // Properties concerning the public state of the game
    private final IntegerProperty percentTicketsRemainingInDeck;
    private final IntegerProperty percentCardsRemainingInDeck;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> routes;

    // Properties concerning the public state for each player
    private final List<IntegerProperty> numberOfTicketsOnHand;
    private final List<IntegerProperty> numberOfCardsOnHand;
    private final List<IntegerProperty> numberOfCarsOnHand;
    private final List<IntegerProperty> numberOfBuildingPointsOnHand;

    //Properties concerning the private state of the player who instantiates ObservableGameState
    private final ListProperty<Ticket> ticketsOnHand;
    private final List<IntegerProperty> numberOfEachCards;
    private final List<BooleanProperty> claimForEachRoute;

    //TODO LE PROF DECRIT UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT,
    //TODO ALORS COMMENT FAIRE POUR FAIRE UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT MAIS QUI PREND EN ARGUMENTS UN ID
    public ObservableGameState(PlayerId id) {
        percentTicketsRemainingInDeck = new SimpleIntegerProperty();
        percentCardsRemainingInDeck = new SimpleIntegerProperty();
        faceUpCards = createFaceUpCards();
        routes = createRoute();

        numberOfTicketsOnHand = createIntPropertyBothPlayers();
        numberOfCardsOnHand = createIntPropertyBothPlayers();
        numberOfCarsOnHand = createIntPropertyBothPlayers();
        numberOfBuildingPointsOnHand = createIntPropertyBothPlayers();

        ticketsOnHand = new SimpleListProperty<>();
        numberOfEachCards = createNumberOfEachCard();
        claimForEachRoute = createClaimForEachRoute();
    }


    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        // refresh the percentTicketsRemainingInDeck
        percentTicketsRemainingInDeck.set((int) Math.floor((newGameState.ticketsCount() / Constants.TICKETS_COUNT) * 100d));

        // refresh the percentCardsRemainingInDeck
        percentCardsRemainingInDeck.set((int) Math.floor((newGameState.cardState().deckSize() / Constants.ALL_CARDS.size()) * 100d));

        // refresh the faceUpCard
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        // refresh the routes
        int i = 0;
        for (ObjectProperty<PlayerId> routeObjectProperty : routes) {
            Route currentRoute = ChMap.routes().get(i);
            if (newGameState.claimedRoutes().contains(currentRoute)) {
                routeObjectProperty.set(newPlayerState.routes().contains(currentRoute) ? playerId : playerId.next());
            }
        }

        // refresh the numberOfTicketsOnHand
        numberOfTicketsOnHand.get(0).set(newPlayerState.ticketCount());
        numberOfTicketsOnHand.get(1).set(newGameState.playerState(playerId.next()).ticketCount());

        // refresh the numberOfCardsOnHand

        numberOfCardsOnHand.get(0).set(newPlayerState.cardCount());
        numberOfCardsOnHand.get(1).set(newGameState.playerState(playerId.next()).cardCount());

        // refresh the numberOfCarsOnHand

        numberOfCarsOnHand.get(0).set(newPlayerState.carCount());
        numberOfCarsOnHand.get(1).set(newGameState.playerState(playerId.next()).carCount());

        // refresh the numberOfBuildingPointsOnHand

        numberOfBuildingPointsOnHand.get(0).set(newPlayerState.claimPoints());
        numberOfBuildingPointsOnHand.get(1).set(newGameState.playerState(playerId.next()).claimPoints());

        // refresh the ticketsOnHand
        ticketsOnHand.set(newPlayerState.tickets().toList()); //TODO JSP FAIRE


        // refresh the numberOfEachCards

        // TODO VERIFIER SI ON A LE DROIT DE CREER DES NOUVELLES CONSTANTES
        for (Card card : Card.ALL) {
            int newNumber = newPlayerState.cards().countOf(card);
            numberOfEachCards.get(card.ordinal()).set(newNumber);
        }


        // refresh the claimForEachRoute
        for (BooleanProperty booleanProperty : claimForEachRoute) {
            if (newGameState.currentPlayerId().equals(playerId)) {
                ListIterator<Route> listIterator = ChMap.routes().listIterator();

                if (listIterator.hasNext()) {
                    Route actualRoute = listIterator.next();
                    if (!newGameState.claimedRoutes().contains(actualRoute)) {
                        List<List<Station>> listClaimedRouteStation = new LinkedList<>();
                        for (Route route : newGameState.claimedRoutes()) {
                            listClaimedRouteStation.add(route.stations());

                        }
                        if (!listClaimedRouteStation.contains(actualRoute.stations())) {
                            if (newPlayerState.canClaimRoute(actualRoute)) {
                                booleanProperty.setValue(true);
                            }
                        } else booleanProperty.setValue(false);

                    } else booleanProperty.setValue(false);
                } else booleanProperty.setValue(false);
            } else booleanProperty.setValue(false);
        }

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
        List<ObjectProperty<Card>> list = new LinkedList<>(); //TODO voir si pas array
        for (int i = 0; i < 5; i++) {
            list.add(new SimpleObjectProperty<Card>());
        }
        return list;
    }


    public final ReadOnlyObjectProperty<PlayerId> routesProperty(int slot) {
        return routes.get(slot);
    }

    public final PlayerId getRoutes(int slot) {
        return routes.get(slot).get();
    }

    private final static List<ObjectProperty<PlayerId>> createRoute() {
        List<ObjectProperty<PlayerId>> objectPropertyList = new LinkedList<>();
        for (Route route : ChMap.routes()) {
            objectPropertyList.add(new SimpleObjectProperty<PlayerId>());
        }
        return objectPropertyList;
    }


    //==============================================================//


    public final ReadOnlyIntegerProperty numberOfTicketsOnHandProperty(int slot) {
        return numberOfTicketsOnHand.get(slot);
    }

    public final int getNumberOfTicketsOnHand(int slot) {
        return numberOfTicketsOnHandProperty(slot).get();
    }


    public final ReadOnlyIntegerProperty numberOfCardsOnHandProperty(int slot) {
        return numberOfCardsOnHand.get(slot);
    }

    public final int getNumberOfCardsOnHand(int slot) {
        return numberOfCardsOnHand.get(slot).get();
    }


    public final ReadOnlyIntegerProperty numberOfCarsOnHandProperty(int slot) {
        return numberOfCarsOnHand.get(slot);
    }

    public final int getNumberOfCarsOnHand(int slot) {
        return numberOfCarsOnHand.get(slot).get();
    }


    public final ReadOnlyIntegerProperty numberOfBuildingPointsOnHandProperty(int slot) {
        return numberOfBuildingPointsOnHand.get(slot);
    }

    public final int getNumberOfBuildingPointsOnHand(int slot) {
        return numberOfBuildingPointsOnHand.get(slot).get();
    }


    private final static List<IntegerProperty> createIntPropertyBothPlayers() {
        List<IntegerProperty> list = new LinkedList<>();
        for (int i = 0; i < 2; i++) list.add(new SimpleIntegerProperty());
        return Collections.unmodifiableList(list);
    }


    //==============================================================//


    public final ReadOnlyListProperty<Ticket> ticketsOnHandProperty() {
        return ticketsOnHand;
    }

    public final Ticket getTicketOnHand(int slot) {
        return ticketsOnHand.get(slot);
    }


    public final ReadOnlyIntegerProperty numberOfEachCardsProperty(int slot) {
        return numberOfEachCards.get(slot);
    }

    public final int getNumberOfEachCards(int slot) {
        return numberOfEachCards.get(slot).get();
    }

    private final static List<IntegerProperty> createNumberOfEachCard() {
        List<IntegerProperty> list = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new SimpleIntegerProperty());
        }
        return Collections.unmodifiableList(list);
    }


    public final ReadOnlyBooleanProperty claimForEachRouteProperty(int slot) {
        return claimForEachRoute.get(slot);
    }

    public final boolean getClaimForEachRoute(int slot) {
        return claimForEachRoute.get(slot).get();
    }

    private final static List<BooleanProperty> createClaimForEachRoute() {
        List<BooleanProperty> list = new LinkedList<>();
        for (Route route : ChMap.routes()) {
            list.add(new SimpleBooleanProperty());
        }
        return list;
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