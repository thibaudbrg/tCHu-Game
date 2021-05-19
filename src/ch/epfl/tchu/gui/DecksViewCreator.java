package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import com.sun.prism.paint.Paint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.*;
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

        ListView<Ticket> tickets = new ListView<>(gameState.ticketsOnHandProperty());
        tickets.setId("tickets");

        HBox handCard = new HBox();
        handCard.setId("hand-pane");

        for (Card c : Card.ALL) {
            StackPane cardAndCount = new StackPane();
            cardAndCount.getStyleClass().add("card");

            Text counter = new Text();
            counter.getStyleClass().add("count");

            List<Node> cardAndCountNodeList = initialiseCard();

            cardAndCount.getChildren().addAll(cardAndCountNodeList);
            cardAndCount.getChildren().add(counter);
            if (c.equals(Card.LOCOMOTIVE)) cardAndCount.getStyleClass().add("NEUTRAL");
            else cardAndCount.getStyleClass().add(c.name());


            cardAndCount.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 0));
            counter.textProperty().bind(Bindings.convert(gameState.numberOfEachCardsProperty(c)));
            counter.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 1));
        }

        /*
        for (Ticket item : tickets.getItems()) {
            if(gameState.ticketsOnHandProperty().contains(item))
                tickets.setBackground(new Background());
        }
        tickets.getItems().get(i)

         */
        HBox HandView = new HBox(tickets, handCard);
        HandView.getStylesheets().add("decks.css");
        HandView.getStylesheets().add("colors.css");


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

        Button ticketDeckButton = createButton(StringsFr.TICKETS, gameState);
        Button cardDeckButton = createButton(StringsFr.CARDS, gameState);


        cardVue.getChildren().add(ticketDeckButton);

        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = new StackPane();
            card.getStyleClass().add("card");
            List<Node> cardNodeList = initialiseCard();
            card.getChildren().addAll(cardNodeList);

            gameState.faceUpCardsProperty(i).addListener((observable, oldValue, newValue) -> {
                card.getStyleClass().add(newValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : newValue.name());
                if (oldValue != null) {
                    card.getStyleClass().remove(oldValue.equals(Card.LOCOMOTIVE) ? "NEUTRAL" : oldValue.name());
                }
            });

            cardVue.getChildren().add(card);

            card.disableProperty().bind(drawCardHandler.isNull());
            card.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(i));
        }

        cardVue.getChildren().add(cardDeckButton);

        ticketDeckButton.disableProperty().bind(drawTicketsHandler.isNull());
        cardDeckButton.disableProperty().bind(drawCardHandler.isNull());
        ticketDeckButton.setOnMouseClicked(s -> drawTicketsHandler.get().onDrawTickets());
        cardDeckButton.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(-1));
        return cardVue;
    }

    private static Button createButton(String text, ObservableGameState gameState) {
        Button button = new Button(text);
        button.getStyleClass().add("gauged");

        Rectangle foregroundRect = new Rectangle();
        foregroundRect.getStyleClass().add("foreground");
        foregroundRect.setHeight(5);

        Rectangle backgroundRect = new Rectangle(50, 5);
        backgroundRect.getStyleClass().add("background");

        Group buttonGauge = new Group(backgroundRect, foregroundRect);
        button.setGraphic(buttonGauge);
        foregroundRect.widthProperty().bind(text.equals(StringsFr.TICKETS) ? gameState.percentTicketsRemainingInDeckProperty().multiply(50).divide(100)
                : gameState.percentCardsRemainingInDeckProperty().multiply(50).divide(100));

        return button;
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
