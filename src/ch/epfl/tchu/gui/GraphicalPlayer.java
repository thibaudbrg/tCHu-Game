package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ch.epfl.tchu.gui.ActionHandler.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.sun.javafx.application.PlatformImpl.isFxApplicationThread;

public final class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;
    private final ObservableList<Text> gameInfos;
    private final Stage  mainStage;

    private ObjectProperty<DrawTicketsHandler> drawTicketsHandlerObject;
    private ObjectProperty<DrawCardHandler> drawCardHandlerObject;
    private ObjectProperty<ClaimRouteHandler> claimRouteHandlerObject;


    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = Map.copyOf(playerNames);
        gameState = new ObservableGameState(playerId);
        gameInfos = FXCollections.observableList(List.of());


        BorderPane borderPane = new BorderPane(
                MapViewCreator.createMapView(gameState, claimRouteHandlerObject, GraphicalPlayer::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(gameState, drawTicketsHandlerObject, drawCardHandlerObject),
                DecksViewCreator.createHandView(gameState),
                InfoViewCreator.createInfoView(this.playerId, this.playerNames, gameState, gameInfos)
        );

        Scene scene = new Scene(borderPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("tCHu \u2014 "+playerNames
        .get(playerId));
        mainStage = stage;
        stage.show();//PEUT ETRE PAS
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (gameInfos.size() >= 5) gameInfos.remove(0);
        gameInfos.add(new Text(message));

    }

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        -
        if (gameState.canDrawTickets()) drawTicketsHandlerObjectProperty().set(() -> {
            drawTicketsHandler.onDrawTickets();
            drawTicketsHandlerObjectProperty().set(null);
            drawCardHandlerObjectProperty().set(null);
            claimRouteHandlerObjectProperty().set(null);
        });
        if (gameState.canDrawCards()) drawCardHandlerObjectProperty().set(slot -> {
            drawCardHandler.onDrawCard(slot);
            drawCard(drawCardHandler);
            drawTicketsHandlerObjectProperty().set(null);
            drawCardHandlerObjectProperty().set(null);
            claimRouteHandlerObjectProperty().set(null);

        });
        claimRouteHandlerObjectProperty().set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            drawTicketsHandlerObjectProperty().set(null);
            drawCardHandlerObjectProperty().set(null);
            claimRouteHandlerObjectProperty().set(null);
        });

    }


    public void chooseTickets(SortedBag<Ticket> ticketSortedBag, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketSortedBag.size() == 3 || ticketSortedBag.size() == 5);
    }


    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHandlerObjectProperty().set(slot -> {
            drawCardHandler.onDrawCard(slot);
            drawTicketsHandlerObjectProperty().set(null);
            drawCardHandlerObjectProperty().set(null);
            claimRouteHandlerObjectProperty().set(null);
        });

    }

    public static void chooseClaimCards(List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

    }

    public static void chooseAdditionalCards() {
        assert isFxApplicationThread();

    }


 private Node constructDialogWindow() {
     Stage dialogStage = new Stage(StageStyle.UTILITY);
     dialogStage.initOwner(mainStage);
     dialogStage.initModality(Modality.WINDOW_MODAL);
VBox vBox = new VBox();
     Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");


        TextFlow textFlow = new TextFlow();
        ListView listView = new ListView();
        Button button = new Button();

        vBox.getChildren().add(textFlow);
        vBox.getChildren().add(listView);
       vBox.getChildren().add(button);
    }


    public DrawTicketsHandler getDrawTicketsHandlerObject() {
        return drawTicketsHandlerObject.get();
    }

    public ObjectProperty<DrawTicketsHandler> drawTicketsHandlerObjectProperty() {
        return drawTicketsHandlerObject;
    }

    public DrawCardHandler getDrawCardHandlerObject() {
        return drawCardHandlerObject.get();
    }

    public ObjectProperty<DrawCardHandler> drawCardHandlerObjectProperty() {
        return drawCardHandlerObject;
    }

    public ClaimRouteHandler getClaimRouteHandlerObject() {
        return claimRouteHandlerObject.get();
    }

    public ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty() {
        return claimRouteHandlerObject;
    }
}
