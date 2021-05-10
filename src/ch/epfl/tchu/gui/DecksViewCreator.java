package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Allows to create the deck view (cards, discards, infos etc...)
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
abstract class DecksViewCreator {

    /**
     * Takes as argument the observableGameState and returns the view of the hand
     *
     * @param gameState (ObservableGameState) The observable GameSate
     * @return (Node) The view of the hand
     */
    public static Node createHandView(ObservableGameState gameState) {
        HBox HandView = new HBox();
        HandView.getStylesheets().add(" decks.css");
        HandView.getStylesheets().add("colors.css");

        ListView<Ticket> billets = new ListView<>(gameState.ticketsOnHandProperty());
        billets.setId("tickets");

        HBox handCard = new HBox();
        handCard.setId("hand-pane");
        for (Card c : Card.ALL) {
            StackPane cardAndCount = new StackPane();
            cardAndCount.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 0));
            if (c.equals(Card.LOCOMOTIVE)) {
                cardAndCount.getStyleClass().add("NEUTRAL");
            } else {
                cardAndCount.getStyleClass().add(c.name());
            }
            cardAndCount.getStyleClass().add("card");
            Text counter = new Text();
            counter.textProperty().bind(Bindings.convert(gameState.numberOfEachCardsProperty(c)));
            counter.getStyleClass().add("count");
            counter.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 1));

            List<Node> cardAndCountNodeList = initialiseCard();

            cardAndCount.getChildren().addAll(cardAndCountNodeList);
            cardAndCount.getChildren().add(counter);
            handCard.getChildren().add(cardAndCount);
        }
        HandView.getChildren().add(billets);
        HandView.getChildren().add(handCard);

        return HandView;
    }

    /**
     * Takes as arguments the observable game state and two properties each containing an action handler:
     * the first contains the one managing the drawing of tickets,
     * the second contains the one managing the drawing of cards
     *
     * @param gameState          (ObservableGameState) The observable GameState
     * @param drawTicketsHandler (ObjectProperty<ActionHandler.DrawTicketsHandler>) The property containing
     *                           the action handler managing the drawing of tickets
     * @param drawCardHandler    (ObjectProperty<ActionHandler.DrawCardHandler>) The property containing
     *                           the action handler managing the drawing of cards
     * @return (Node) The view of the hand
     */
    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandler,
                                       ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandler) {


        VBox cardVue = new VBox();
        cardVue.setId("card-pane");
        cardVue.getStylesheets().add("decks.css");
        cardVue.getStylesheets().add("colors.css");

        Button ticketDeckButton = new Button(StringsFr.TICKETS);
        ticketDeckButton.getStyleClass().add("gauged");


        Rectangle foregroundRect1 = new Rectangle();
        foregroundRect1.getStyleClass().add("foreground");
        foregroundRect1.setHeight(5);
        foregroundRect1.widthProperty().bind(
                gameState.percentTicketsRemainingInDeckProperty().multiply(50).divide(100));

        Rectangle backgroundRect1 = new Rectangle(50, 5);
        backgroundRect1.getStyleClass().add("background");

        Group button1Jauge = new Group(backgroundRect1, foregroundRect1);

        ticketDeckButton.setGraphic(button1Jauge);
        Button cardDeckButton = new Button(StringsFr.CARDS);
        cardDeckButton.getStyleClass().add("gauged");


        Rectangle foregroundRect2 = new Rectangle();
        foregroundRect2.getStyleClass().add("foreground");
        foregroundRect2.setHeight(5);
        foregroundRect2.widthProperty().bind(
                gameState.percentCardsRemainingInDeckProperty().multiply(50).divide(100));

        Rectangle backgroundRect2 = new Rectangle(50, 5);
        backgroundRect2.getStyleClass().add("background");

        Group button2Jauge = new Group(backgroundRect2, foregroundRect2);
        cardDeckButton.setGraphic(button2Jauge);

        cardVue.getChildren().add(ticketDeckButton);

        ticketDeckButton.disableProperty().bind(drawTicketsHandler.isNull());
        cardDeckButton.disableProperty().bind(drawCardHandler.isNull());

        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = new StackPane();
            gameState.faceUpCardsProperty(i).addListener((observable, oldValue, newValue) -> {
                card.getStyleClass().add(newValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newValue.name());
                if (oldValue != null) {
                    card.getStyleClass().remove(oldValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : oldValue.name());
                }


            });
            card.getStyleClass().add("card");
            List<Node> cardNodeList = initialiseCard();
            card.getChildren().addAll(cardNodeList);
            cardVue.getChildren().add(card);
card.disableProperty().bind(drawCardHandler.isNull());
            card.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(i));
        }
        cardVue.getChildren().add(cardDeckButton);

        ticketDeckButton.setOnMouseClicked(s -> drawTicketsHandler.get().onDrawTickets());
        cardDeckButton.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(-1)
        );
        return cardVue;
    }


    private static List<Node> initialiseCard() {
        List<Rectangle> list = new LinkedList<>();

        Rectangle rectangleOutside = new Rectangle(60, 90);
        rectangleOutside.getStyleClass().add("outside");

        Rectangle rectangleInside = new Rectangle(40, 70);
        rectangleInside.getStyleClass().add("filled");

        Rectangle rectangleImage = new Rectangle(40, 70);
        rectangleImage.getStyleClass().add("train-image");

        list.add(rectangleOutside);
        list.add(rectangleInside);
        list.add(rectangleImage);

        return Collections.unmodifiableList(list);
    }
}
