package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;

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
import javafx.util.Callback;
import javafx.util.Duration;

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

    private final static int MIN_CARD_TEXT_VISIBLE_PROPERTY = 1;
    private final static int MIN_CARD_VISIBLE_PROPERTY = 0;
    private final static int BUTTON_WIDTH = 50;
    private final static int BUTTON_HEIGHT = 5;

    private final static int CARD_WIDTH = 60;

    private final static int CARD_HEIGHT = 90;
    private final static int CARD_WIDTH_INSIDE = 40;
    private final static int CARD_HEIGHT_INSIDE = 70;

    /**
     * Takes as argument the observableGameState and returns the view of the hand
     *
     * @param gameState (ObservableGameState) The observable GameSate
     * @return (Node) The view of the hand
     */
    public static Node createHandView(ObservableGameState gameState) {

        ListView<Ticket> tickets = new ListView<>(gameState.ticketsOnHandProperty());
        tickets.setId("tickets");
        tickets.setCellFactory(new Callback<>() {
                                   @Override
                                   public ListCell<Ticket> call(ListView<Ticket> param) {
                                       return new ListCell<>() {
                                           @Override
                                           protected void updateItem(Ticket item, boolean empty) {
                                               super.updateItem(item, empty);
                                               BooleanProperty trueProperty = new SimpleBooleanProperty();
                                               trueProperty.set(true);
                                               if (!empty) {
                                                   textProperty().bind(Bindings
                                                           .when(gameState.ticketCompleteProperty(item).isEqualTo(trueProperty))
                                                           .then(item.text() + "  \u2713")
                                                           .otherwise(item.text() + "  \u2717"));
                                                   styleProperty().bind(Bindings
                                                           .when(gameState.ticketCompleteProperty(item).isEqualTo(trueProperty))
                                                           .then("-fx-background-color: D7FBED;")
                                                           .otherwise("-fx-background-color: FFD1D0;"));

                                               }
                                           }
                                       };
                                   }
                               }
        );


        HBox handCard = new HBox();
        handCard.setId("hand-pane");

        for (Card c : Card.ALLEXTENDED) {
            StackPane cardAndCount = new StackPane();
            cardAndCount.getStyleClass().add("card");

            Text counter = new Text();
            counter.getStyleClass().add("count");

            List<Node> cardAndCountNodeList = initialiseCard();

            cardAndCount.getChildren().addAll(cardAndCountNodeList);
            cardAndCount.getChildren().add(counter);
            if (c.equals(Card.LOCOMOTIVE)) cardAndCount.getStyleClass().add(StringsFr.NEUTRAL);
            else cardAndCount.getStyleClass().add(c.name());


            cardAndCount.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), MIN_CARD_VISIBLE_PROPERTY));
            counter.textProperty().bind(Bindings.convert(gameState.numberOfEachCardsProperty(c)));
            counter.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfEachCardsProperty(c), MIN_CARD_TEXT_VISIBLE_PROPERTY));

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
                if (oldValue == null || gameState.numberOfCardChanged()) {
                    if (oldValue != null) {
                        card.getStyleClass().remove(oldValue.equals(Card.LOCOMOTIVE)?StringsFr.NEUTRAL:oldValue.name());
                    }
                    card.getStyleClass().add(newValue.equals(Card.LOCOMOTIVE) ? StringsFr.NEUTRAL : newValue.name());
                }

            });

            cardVue.getChildren().add(card);
            BooleanProperty disable = new SimpleBooleanProperty();

            card.disableProperty().bind(drawCardHandler.isNull().or(disable));
            card.setOnMouseClicked(s -> {
                drawCardHandler.get().onDrawCard(i);
                disable.set(true);
                TranslateTransition transition1 = new TranslateTransition(Duration.seconds(2), card);
                transition1.setNode(card);
                transition1.setToX(-600);
                switch (i) {
                    case 0:
                        transition1.setToY(200);
                        break;
                    case 1:
                        transition1.setToY(100);
                        break;
                    case 2:
                        transition1.setToY(0);
                        break;
                    case 3:
                        transition1.setToY(-100);
                        break;
                    case 4:
                        transition1.setToY(-200);
                        break;
                }
                transition1.setAutoReverse(true);
                transition1.setCycleCount(2);
                transition1.play();


                ScaleTransition transition2 = new ScaleTransition(Duration.seconds(2), card);
                transition2.setNode(card);
                transition2.setToX(4);
                transition2.setToY(4);
                transition2.setAutoReverse(true);
                transition2.setCycleCount(2);
                transition2.play();


                RotateTransition transition3 = new RotateTransition(Duration.seconds(2), card);
                transition3.setFromAngle(0);
                transition3.setToAngle(360);
                transition3.play();

                transition1.setOnFinished(event -> {
                    disable.set(false);
                    for (Card c : Card.ALLEXTENDED) card.getStyleClass().remove(c.name());
                    card.getStyleClass().remove(StringsFr.NEUTRAL);
                    card.getStyleClass().add(gameState.faceUpCardsProperty(i).get().equals(Card.LOCOMOTIVE) ? StringsFr.NEUTRAL : gameState.faceUpCardsProperty(i).get().name());
                });
            });

        }

        cardVue.getChildren().add(cardDeckButton);

        ticketDeckButton.disableProperty().bind(drawTicketsHandler.isNull());
        cardDeckButton.disableProperty().bind(drawCardHandler.isNull());
        ticketDeckButton.setOnMouseClicked(s -> drawTicketsHandler.get().onDrawTickets());
        cardDeckButton.setOnMouseClicked(s -> drawCardHandler.get().onDrawCard(Constants.DECK_SLOT));

        return cardVue;
    }

    private static Button createButton(String text, ObservableGameState gameState) {
        Button button = new Button(text);
        button.getStyleClass().add("gauged");

        Rectangle foregroundRect = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        foregroundRect.getStyleClass().add("foreground");


        Rectangle backgroundRect = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        backgroundRect.getStyleClass().add("background");

        Group buttonGauge = new Group(backgroundRect, foregroundRect);
        button.setGraphic(buttonGauge);
        foregroundRect.widthProperty().bind(text.equals(StringsFr.TICKETS) ? gameState.percentTicketsRemainingInDeckProperty().multiply(BUTTON_WIDTH).divide(100)
                : gameState.percentCardsRemainingInDeckProperty().multiply(BUTTON_WIDTH).divide(100));

        return button;
    }

    private static List<Node> initialiseCard() {
        List<Rectangle> list = new LinkedList<>();

        Rectangle rectangleOutside = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        rectangleOutside.getStyleClass().add("outside");

        Rectangle rectangleInside = new Rectangle(CARD_WIDTH_INSIDE, CARD_HEIGHT_INSIDE);
        rectangleInside.getStyleClass().add("filled");
        rectangleInside.getStyleClass().add("inside");

        Rectangle rectangleImage = new Rectangle(CARD_WIDTH_INSIDE, CARD_HEIGHT_INSIDE);
        rectangleImage.getStyleClass().add("train-image");

        list.add(rectangleOutside);
        list.add(rectangleInside);
        list.add(rectangleImage);

        return Collections.unmodifiableList(list);
    }
}
