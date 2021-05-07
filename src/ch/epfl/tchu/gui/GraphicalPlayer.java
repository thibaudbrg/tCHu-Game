package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.ActionHandler.*;
import static com.sun.javafx.application.PlatformImpl.isFxApplicationThread;

public final class GraphicalPlayer {
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;
    private final ObservableList<Text> gameInfos;
    private static Stage mainStage = new Stage() ;

    private ObjectProperty<DrawTicketsHandler> drawTicketsHandlerObject = new SimpleObjectProperty<>();
    private ObjectProperty<DrawCardHandler> drawCardHandlerObject =new SimpleObjectProperty<>();
    private ObjectProperty<ClaimRouteHandler> claimRouteHandlerObject = new SimpleObjectProperty<>();


    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = Map.copyOf(playerNames);
        gameState = new ObservableGameState(playerId);
        gameInfos = FXCollections.observableList(new ArrayList<>());

        Stage stage = new Stage();
        stage.setTitle("tCHu \u2014 " + playerNames
                .get(playerId));
        mainStage = stage;

        BorderPane borderPane = new BorderPane(
                MapViewCreator.createMapView(gameState, claimRouteHandlerObject, GraphicalPlayer::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(gameState, drawTicketsHandlerObject, drawCardHandlerObject),
                DecksViewCreator.createHandView(gameState),
                InfoViewCreator.createInfoView(this.playerId, this.playerNames, gameState, gameInfos)
        );

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();//TODO PEUT ETRE PAS
    }

    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

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

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (gameInfos.size() >= 5) gameInfos.remove(0);
        gameInfos.add(new Text(message));

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

    public void chooseTickets(SortedBag<Ticket> ticketSortedBag, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketSortedBag.size() == 3 || ticketSortedBag.size() == 5);
        constructDialogWindowTicket(ticketSortedBag, chooseTicketsHandler);
    }

    public static void chooseClaimCards(List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        constructDialogWindowCards(0, cards, chooseCardsHandler);

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        constructDialogWindowCards(1, cards, chooseCardsHandler);

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

    private void constructDialogWindowTicket(SortedBag<Ticket> ticketSortedBag, ChooseTicketsHandler chooseTicketsHandler) {
        int numberOfTicketToClaim = ticketSortedBag.size() == 3 ? 1 : 0;
        ObservableList<Ticket> observableListTickets = FXCollections.observableList(ticketSortedBag.toList());
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setOnCloseRequest((s) -> s.consume());
        dialogStage.setTitle(StringsFr.TICKETS_CHOICE);
        dialogStage.initOwner(mainStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, numberOfTicketToClaim, StringsFr.plural(numberOfTicketToClaim)));
        TextFlow textFlow = new TextFlow(text);
        ListView<Ticket> listView = new ListView(observableListTickets);
        Button button = new Button(StringsFr.CHOOSE);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        button.disableProperty().bind(Bindings.greaterThan(numberOfTicketToClaim, Bindings.size(listView.getSelectionModel().getSelectedItems())));
        button.setOnAction(s -> {
            dialogStage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });

        VBox vBox = new VBox(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        dialogStage.setScene(scene);
        dialogStage.show();

    }

    private static void constructDialogWindowCards(int i, List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        Preconditions.checkArgument(i == 1 || i == 0);

        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setOnCloseRequest((s) -> s.consume());

        dialogStage.setTitle(StringsFr.CARDS_CHOICE);
        dialogStage.initOwner(mainStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Text text = new Text(i == 0 ? StringsFr.CHOOSE_CARDS : StringsFr.CHOOSE_ADDITIONAL_CARDS);
        TextFlow textFlow = new TextFlow(text);
        ListView<SortedBag<Card>> listView = new ListView();
        Button button = new Button(StringsFr.CHOOSE);
        if (i == 0)
            button.disableProperty().bind(Bindings.equal(1, Bindings.size(listView.getSelectionModel().getSelectedItems())));

        listView.setCellFactory(v -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(SortedBag bag) {
                return Info.cardDescription(bag);
            }

            @Override
            public SortedBag fromString(String string) {
                throw new UnsupportedOperationException();
            }
        }));
        button.setOnAction(s -> {
            dialogStage.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());

        });
        VBox vBox = new VBox(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        dialogStage.setScene(scene);
        dialogStage.show();

    }
}
