package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;


public final class DisplayTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        var faceUpCards = SortedBag.of(5, Card.LOCOMOTIVE).toList();
        var cardState = new PublicCardState(faceUpCards, 54, 0);
        var initialPlayerState = (PublicPlayerState) PlayerState.initial(SortedBag.of(4, Card.RED));
        var playerState = Map.of(
                PLAYER_1, initialPlayerState,
                PLAYER_2, initialPlayerState);
        var ticketsCount = 36;
        var p1 = PLAYER_1;
        var p2 = PLAYER_2;
        PublicGameState publicGameState = new PublicGameState(ticketsCount, cardState, p1, playerState, p2);
        var builder = new SortedBag.Builder<Card>();
        builder.add(2, Card.BLUE);
        builder.add(2, Card.GREEN);
        PlayerState ownState = PlayerState.initial(builder.build());
        ObservableGameState gameState = new ObservableGameState(PLAYER_1);
          gameState.setState(publicGameState, ownState);
       /* ObjectProperty<ClaimRouteHandler> claimRoute =
                new SimpleObjectProperty<>(Stage9Test::claimRoute);
        ObjectProperty<DrawTicketsHandler> drawTickets =
                new SimpleObjectProperty<>(Stage9Test::drawTickets);
        ObjectProperty<DrawCardHandler> drawCard =
                new SimpleObjectProperty<>(Stage9Test::drawCard);*/

        Node mapView = MapViewCreator
                .createMapView(gameState);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState);
        Node handView = DecksViewCreator
                .createHandView(gameState);

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, null);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

        setState(gameState);
    }

    private void setState(ObservableGameState gameState) {
        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);
        gameState.setState(publicGameState, p1State);
    }

    private static void claimRoute(Route route, SortedBag<Card> cards) {
        System.out.printf("Prise de possession d'une route : %s - %s %s%n",
                route.station1(), route.station2(), cards);
    }

  /*  private static void chooseCards(List<SortedBag<Card>> options,
                                    ChooseCardsHandler chooser) {
        chooser.onChooseCards(options.get(0));
    }*/

    private static void drawTickets() {
        System.out.println("Tirage de billets !");
    }

    private static void drawCard(int slot) {
        System.out.printf("Tirage de cartes (emplacement %s)!\n", slot);
    }
}
