package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
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
import java.util.Objects;

import static ch.epfl.tchu.gui.ActionHandler.*;
import static javafx.application.Platform.isFxApplicationThread;


/**
 * Represents the graphical interface of a tCHu player
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class GraphicalPlayer {
    private final ObservableGameState gameState;
    private final ObservableList<Text> gameInfos;
    private final Stage mainStage;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHandlerObject = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardHandlerObject = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteHandlerObject = new SimpleObjectProperty<>();


    public static int INFO_LIST_MAX_SIZE = 5;
    /**
     * Constructs the graphical interface
     *
     * @param playerId    (PlayerId) The Id's players
     * @param playerNames (Map<PlayerId, String>) The playerNames
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        isFxApplicationThread();

        Map<PlayerId, String> playerNames1 = Objects.requireNonNull(Map.copyOf(playerNames));
        gameState = new ObservableGameState(playerId);
        gameInfos = FXCollections.observableList(new ArrayList<>());

        Stage stage = new Stage();
        stage.setTitle("tCHu \u2014 " + playerNames.get(playerId));
        stage.getIcons().add(new Image("map.png"));
        this.mainStage = stage;

        BorderPane borderPane = new BorderPane(
                MapViewCreator.createMapView(gameState, claimRouteHandlerObject, this::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(gameState, drawTicketsHandlerObject, drawCardHandlerObject),
                DecksViewCreator.createHandView(gameState),
                InfoViewCreator.createInfoView(playerId, playerNames1, gameState, gameInfos));
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Makes sure that when the player decides to perform an action, the corresponding handler is called
     *
     * @param drawTicketsHandler (DrawTicketsHandler) The action handler corresponding to the drawing of tickets
     * @param drawCardHandler    (DrawCardHandler) The action handler corresponding to the drawing of cards
     * @param claimRouteHandler  (ClaimRouteHandler) The action handler corresponding to the claim of a route
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        if (gameState.canDrawTickets()) drawTicketsHandlerObject.set(() -> {
            drawTicketsHandler.onDrawTickets();
            putAllHandlersAtNull();
        });
        if (gameState.canDrawCards()) drawCardHandlerObject.set(slot -> {
            drawCardHandler.onDrawCard(slot);
            putAllHandlersAtNull();
            drawCard(drawCardHandler);

        });
        claimRouteHandlerObject.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            putAllHandlersAtNull();
        });

    }

    /**
     * Calls setState(...) on the player's observable state
     *
     * @param newGameState   (PublicGameState) The new PublicGameState
     * @param newPlayerState (PlayerState) The new PlayerState
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * Adds to the bottom of the information the message about the progress of the game,
     * which are presented in the lower part of the information view,
     * this view contains only the last five messages received
     *
     * @param message (String) The corresponding message
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (gameInfos.size() >= INFO_LIST_MAX_SIZE) gameInfos.remove(0);
        gameInfos.add(new Text(message));

    }

    /**
     * Allows the player to choose a car/locomotive card, either one of the five face-up cards or the one at the top of the deck;
     * once the player has clicked on one of these cards, the handler is called up with the player's choice;
     * this method is intended to be called up when the player has already drawn a first card and must now draw the second
     *
     * @param drawCardHandler (DrawCardHandler) The handler for the draw of cards
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardHandlerObject.set(slot -> {
            drawCardHandler.onDrawCard(slot);
            putAllHandlersAtNull();
        });
    }


    /**
     * Open a window, which enables the player to select the cards that he wants to use to claim a road
     *
     * @param cards              (List<SortedBag<Card>>) The possible claim Cards
     * @param chooseCardsHandler (ChooseCardsHandler) The handler for the cards to choose
     */
    public void chooseClaimCards(List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        constructDialogWindowCards(0, cards, chooseCardsHandler);

    }

    /**
     * Open a window, which enables the player to select the additional cards to use.
     *
     * @param cards              (List<SortedBag<Card>>) The possible additional cards
     * @param chooseCardsHandler (ChooseCardsHandler) The handler for the cards to choose
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        constructDialogWindowCards(1, cards, chooseCardsHandler);

    }

    /**
     * Open a window, which enables the player to select tickets from a given list
     *
     * @param ticketSortedBag      (SortedBag<Ticket>) The possible tickets to choose
     * @param chooseTicketsHandler (ChooseTicketsHandler) Then handler for the tickets to choose
     */
    public void chooseTickets(SortedBag<Ticket> ticketSortedBag, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketSortedBag.size() == Constants.IN_GAME_TICKETS_COUNT || ticketSortedBag.size() == Constants.INITIAL_TICKETS_COUNT);
        constructDialogWindowTicket(ticketSortedBag, chooseTicketsHandler);
    }

    private void putAllHandlersAtNull() {
        drawTicketsHandlerObject.set(null);
        drawCardHandlerObject.set(null);
        claimRouteHandlerObject.set(null);
    }

    private void constructDialogWindowTicket(SortedBag<Ticket> ticketSortedBag, ChooseTicketsHandler chooseTicketsHandler) {
        int numberOfTicketToClaim = ticketSortedBag.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        ObservableList<Ticket> observableListTickets = FXCollections.observableList(ticketSortedBag.toList());

        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setOnCloseRequest(Event::consume);
        dialogStage.setTitle(StringsFr.TICKETS_CHOICE);
        dialogStage.initOwner(mainStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, numberOfTicketToClaim, StringsFr.plural(numberOfTicketToClaim)));
        TextFlow textFlow = new TextFlow(text);
        ListView<Ticket> listView = new ListView<>(observableListTickets);
        Button button = new Button(StringsFr.CHOOSE);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        button.disableProperty().bind(Bindings.greaterThan(numberOfTicketToClaim, Bindings.size(listView.getSelectionModel().getSelectedItems()))); //TODO comprends pas
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

    private void constructDialogWindowCards(int i, List<SortedBag<Card>> cards, ChooseCardsHandler chooseCardsHandler) {
        Preconditions.checkArgument(i == 1 || i == 0);
        ObservableList<SortedBag<Card>> sortedBagObservableList = FXCollections.observableList(cards);
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.setOnCloseRequest(Event::consume);

        dialogStage.setTitle(StringsFr.CARDS_CHOICE);
        dialogStage.initOwner(mainStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Text text = new Text(i == 0 ? StringsFr.CHOOSE_CARDS : StringsFr.CHOOSE_ADDITIONAL_CARDS);
        TextFlow textFlow = new TextFlow(text);
        ListView<SortedBag<Card>> listView = new ListView<>(sortedBagObservableList);
        Button button = new Button(StringsFr.CHOOSE);
        if (i == 0) {
            button.disableProperty().bind(Bindings.equal(0, Bindings.size(listView.getSelectionModel().getSelectedItems())));
        }

        listView.setCellFactory(v -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(SortedBag<Card> bag) {
                return Info.cardDescription(bag);
            }

            @Override
            public SortedBag<Card> fromString(String string) {
                throw new UnsupportedOperationException();
            }
        }));
        button.setOnAction(s -> {
            dialogStage.hide();
            SortedBag<Card> chooseCard = listView.getSelectionModel().getSelectedItem();
            chooseCardsHandler.onChooseCards(Objects.requireNonNullElseGet(chooseCard, SortedBag::of));
        });
        VBox vBox = new VBox(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        dialogStage.setScene(scene);
        dialogStage.show();
    }
}
