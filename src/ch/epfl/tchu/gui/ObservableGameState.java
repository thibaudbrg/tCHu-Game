package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;
import static ch.epfl.tchu.game.Constants.TYPE_CARD_SLOTS;

public final class ObservableGameState {
    // Properties concerning the public state of the game
    private final IntegerProperty percentTicketsRemainingInDeck = new SimpleIntegerProperty();
    private final IntegerProperty percentCardsRemainingInDeck = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards = new createFaceUpCards();
    private final List<ObjectProperty<Route>> routes = new SimpleListProperty();// l'identité du joueur la possédant, ou null si elle n'appartient à personne ???

    // Properties concerning the public state for each player
    private final IntegerProperty numberOfTicketsOnHand = new SimpleIntegerProperty();
    private final IntegerProperty numberOfCardsOnHand = new SimpleIntegerProperty();
    private final IntegerProperty numberOfCarsOnHand = new SimpleIntegerProperty();
    private final IntegerProperty numberOfBuildingPointsOnHand = new SimpleIntegerProperty();

    //Properties concerning the private state of the player who instanciates ObservableGameState
    private final List<ObjectProperty<Ticket>> ticketsOnHand = new SimpleListProperty();
    private final List<IntegerProperty> numberOfEachCards = new SimpleListProperty();
    private final List<BooleanProperty> claimForEachRoute = new SimpleListProperty();

    //TODO LE PROF DECRIT UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT,
    //TODO ALORS COMMENT FAIRE POUR FAIRE UN CONSTRUCTEUR PAR DEFAUT PAR DEFAUT MAIS QUI PREND EN ARGUMENTS UN ID
    public ObservableGameState(PlayerId id) {

    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        // refresh the percentTicketsRemainingInDeck
        percentTicketsRemainingInDeckProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        percentTicketsRemainingInDeck.set((int) Math.floor((newGameState.ticketsCount() / Constants.TICKETS_COUNT) * 100d));

        // refresh the percentCardsRemainingInDeck
        percentCardsRemainingInDeckProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        percentCardsRemainingInDeck.set((int) Math.floor((newGameState.cardState().deckSize() / Constants.ALL_CARDS.size()) * 100d));

        // refresh the faceUpCards
        faceUpCardsProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        // refresh the routes
        routesProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        for (Route route : newGameState.claimedRoutes()) {
            if (/*route appartient à player ? */) {
                Route newRoute = route;
                routes.get().set(newRoute);

            } else {
                Route newRoute = null;
                routes.get().set(newRoute);
            }
        }


        // refresh the numberOfTicketsOnHand
        numberOfTicketsOnHandProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        numberOfTicketsOnHand.set(newPlayerState.ticketCount());

        // refresh the numberOfCardsOnHand
        numberOfCardsOnHandProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        numberOfCardsOnHand.set(newPlayerState.cards().size());

        // refresh the numberOfCarsOnHand
        numberOfCarsOnHandProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        numberOfCarsOnHand.set(newPlayerState.carCount());

        // refresh the numberOfBuildingPointsOnHand
        numberOfBuildingPointsOnHandProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        numberOfBuildingPointsOnHand.set(newPlayerState.claimPoints());


        // refresh the ticketsOnHand
        ticketsOnHandProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        for (Ticket ticket : newPlayerState.tickets()) {
            Ticket newTicket = ticket;
            ticketsOnHand.get().set(newTicket);
        }

        // refresh the numberOfEachCards
        numberOfEachCardsProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        // TODO VERIFIER SI ON A LE DROIT DE CREER DES NOUVELLES CONSTANTES
        for (int slot : TYPE_CARD_SLOTS) {
            // TODO VERIFIER SI LE STREAM MARCHE
            int newNumber = Collections.frequency(newPlayerState.cards().toList(), Card.CARS.get(slot));
            numberOfEachCards.get(slot).addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
            numberOfEachCards.get(slot).set(newNumber);
        }


        // refresh the claimForEachRoute
        claimForEachRouteProperty().addListener((observableValue, oldValue, newValue) -> System.out.println(newValue));
        for (int slot : )
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


    public final List<ObjectProperty<Card>> faceUpCardsProperty() {
        return Collections.unmodifiableList(faceUpCards);
    }

    public final ReadOnlyObjectProperty<Card> getFaceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    // TODO C'est quoi cette méthode
    private static List<SimpleObjectProperty<Card>> createFaceUpCards() {
        List<SimpleObjectProperty<Card>> list = new ArrayList<>();

        return null;
    }


    public final List<ObjectProperty<Route>> routesProperty() {
        return Collections.unmodifiableList(routes);
    }

    public final ReadOnlyObjectProperty<Route> getRoutes(int slot) {
        return routes.get(slot);
    }


    //==============================================================//


    public final ReadOnlyIntegerProperty numberOfTicketsOnHandProperty() {
        return numberOfTicketsOnHand;
    }

    public final int getNumberOfTicketsOnHand() {
        return numberOfTicketsOnHand.get();
    }


    public final ReadOnlyIntegerProperty numberOfCardsOnHandProperty() {
        return numberOfCardsOnHand;
    }

    public final int getNumberOfCardsOnHand() {
        return numberOfCardsOnHand.get();
    }


    public final ReadOnlyIntegerProperty numberOfCarsOnHandProperty() {
        return numberOfCarsOnHand;
    }

    public final int getNumberOfCarsOnHand() {
        return numberOfCarsOnHand.get();
    }


    public final ReadOnlyIntegerProperty numberOfBuildingPointsOnHandProperty() {
        return numberOfBuildingPointsOnHand;
    }

    public final int getNumberOfBuildingPointsOnHand() {
        return numberOfBuildingPointsOnHand.get();
    }


    //==============================================================//


    public final List<ObjectProperty<Ticket>> ticketsOnHandProperty() {
        return Collections.unmodifiableList(ticketsOnHand);
    }

    public final ReadOnlyObjectProperty<Ticket> getTicketOnHand(int slot) {
        return ticketsOnHand.get(slot);
    }

    
    public final List<IntegerProperty> numberOfEachCardsProperty() {
        return numberOfEachCards;
    }

    public final ReadOnlyIntegerProperty getNumberOfEachCards(int slot) {
        return numberOfEachCards.get(slot);
    }


    public final List<BooleanProperty> claimForEachRouteProperty() {
        return claimForEachRoute;
    }

    public final ReadOnlyBooleanProperty getClaimForEachRoute(int slot) {
        return claimForEachRoute.get(slot);
    }


    //==============================================================//


    // TODO Comment on code ces trois classes
    private boolean canDrawTickets() {
        return PublicGameState.canDrawTickets();
    }

    private boolean canDrawCards() {
        return PublicGameState.canDrawCards();
    }

    private static List<SortedBag<Card>> possibleClaimCards(Route route) {
        return PlayerState.possibleClaimCards(route);
    }
}

// https://openjfx.io/openjfx-docs/#install-javafx