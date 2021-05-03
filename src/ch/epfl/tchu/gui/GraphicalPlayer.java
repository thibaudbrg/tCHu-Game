package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.swing.text.html.ListView;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.sun.javafx.application.PlatformImpl.isFxApplicationThread;

public final class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;
    private final ObservableList<Text> gameInfos;

    private ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerObject;
    private ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerObject;
    private ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerObject;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = Map.copyOf(playerNames);
        gameState = new ObservableGameState(playerId);
        gameInfos = FXCollections.observableList(List.of());

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        Stage dialogStage = new Stage();


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

    public void startTurn(ActionHandler.DrawTicketsHandler drawTicketsHandler,
                          ActionHandler.DrawCardHandler drawCardHandler,
                          ActionHandler.ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        if (gameState.canDrawTickets()) drawTicketsHandlerObjectProperty().set(drawTicketsHandler);
        if (gameState.canDrawCards()) drawCardHandlerObjectProperty().set(drawCardHandler);
        claimRouteHandlerObjectProperty().set(claimRouteHandler); //TODO pas compris le bail avec presque les memes reset les propriétés


    }


    public void chooseTickets(SortedBag<Ticket> ticketSortedBag, ActionHandler.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketSortedBag.size() == 3 || ticketSortedBag.size() == 5);
    }


    public void drawCard(ActionHandler.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        //drawCardHandlerObjectProperty().bindBidirectional();

    }

    public void chooseClaimCards() {
        assert isFxApplicationThread();

    }

    public void chooseAdditionalCards() {
        assert isFxApplicationThread();

    }


 /*   private Node constructDialogWindow() {

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");

        Stage dialogStage = new Stage();


        TextFlow textFlow = new TextFlow();
        ListView listView = new ListView();
        Button button = new Button();

        vBox.getChildren().add(textFlow);
        vBox.getChildren().add(listView);
       vBox.getChildren().add(button);
    }*/





    public ActionHandler.DrawTicketsHandler getDrawTicketsHandlerObject() {
        return drawTicketsHandlerObject.get();
    }

    public ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerObjectProperty() {
        return drawTicketsHandlerObject;
    }

    public ActionHandler.DrawCardHandler getDrawCardHandlerObject() {
        return drawCardHandlerObject.get();
    }

    public ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerObjectProperty() {
        return drawCardHandlerObject;
    }

    public ActionHandler.ClaimRouteHandler getClaimRouteHandlerObject() {
        return claimRouteHandlerObject.get();
    }

    public ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHandlerObjectProperty() {
        return claimRouteHandlerObject;
    }
}
