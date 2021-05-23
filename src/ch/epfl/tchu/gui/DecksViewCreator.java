package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Callback;

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
        tickets.setCellFactory(new Callback<ListView<Ticket>, ListCell<Ticket>>() {
                                   @Override
                                   public ListCell<Ticket> call(ListView<Ticket> param) {
                                       return new ListCell<Ticket>() {
                                           @Override
                                           protected void updateItem(Ticket item, boolean empty) {
                                               super.updateItem(item, empty);
                                               BooleanProperty trueProperty = new SimpleBooleanProperty();
                                               trueProperty.set(true);
                                               if(!empty) {
                                                  // setOnMouseClicked((s)->setText());
                                                    textProperty().bind(Bindings.when(gameState.ticketDoneProperty(item).isEqualTo(trueProperty)).then(item.text()+"  \u2713").otherwise(item.text()+"  \u2717"));
                                                     styleProperty().bind(Bindings.when(gameState.ticketDoneProperty(item).isEqualTo(trueProperty)).then("-fx-border-color: B0F2B6;").otherwise("-fx-border-color: FF6961;"));
                                               } }
                                       };
                                   }
                               }
        );


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
            if (c.equals(Card.LOCOMOTIVE)) cardAndCount.getStyleClass().add(StringsFr.NEUTRAL);
            else cardAndCount.getStyleClass().add(c.name());


            cardAndCount.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 0));
            counter.textProperty().bind(Bindings.convert(gameState.numberOfEachCardsProperty(c)));
            counter.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), 1));

            handCard.getChildren().add(cardAndCount);
        }

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
                card.getStyleClass().add(newValue.equals(Card.LOCOMOTIVE) ? StringsFr.NEUTRAL : newValue.name());
                if (oldValue != null) {
                    card.getStyleClass().remove(oldValue.equals(Card.LOCOMOTIVE) ? StringsFr.NEUTRAL : oldValue.name());
                }
            });

            cardVue.getChildren().add(card);

            card.disableProperty().bind(drawCardHandler.isNull());
            card.setOnMouseClicked(s -> {drawCardHandler.get().onDrawCard(i);
               

            });
        }

        cardVue.getChildren().add(cardDeckButton);

        ticketDeckButton.disableProperty().bind(drawTicketsHandler.isNull());
        cardDeckButton.disableProperty().bind(drawCardHandler.isNull());
        ticketDeckButton.setOnMouseClicked(s -> drawTicketsHandler.get().onDrawTickets());
        cardDeckButton.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(-1)
        );

        return cardVue;
    }

    private static Button createButton(String text, ObservableGameState gameState) {
        Button button = new Button(text);
        button.getStyleClass().add("gauged");

        Rectangle foregroundRect = new Rectangle(50, 5);
        foregroundRect.getStyleClass().add("foreground");


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
        rectangleInside.getStyleClass().add("inside");

        Rectangle rectangleImage = new Rectangle(40, 70);
        rectangleImage.getStyleClass().add("train-image");

        list.add(rectangleOutside);
        list.add(rectangleInside);
        list.add(rectangleImage);

        return Collections.unmodifiableList(list);
    }
}
