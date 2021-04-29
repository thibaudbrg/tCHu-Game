package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
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


class DecksViewCreator {

    public static Node createHandView(ObservableGameState gameState) { //TODO changer  retour
        // Normalement pas dans la methode
        HBox HandView = new HBox();
        HandView.getStylesheets().add(" decks.css");
        HandView.getStylesheets().add("colors.css");

        ListView<Ticket> billets = new ListView<>(gameState.ticketsOnHandProperty());
        billets.setId("tickets");

        HBox handCard = new HBox();
        handCard.setId("hand-pane");
        for (Card c : Card.values()) {
            StackPane cardAndCount = new StackPane();
            if (c.equals(Card.LOCOMOTIVE)) {
                cardAndCount.getStyleClass().add("NEUTRAL");
            } else {
                cardAndCount.getStyleClass().add(c.name());
            }
            cardAndCount.getStyleClass().add("card");

            Text counter = new Text();
            counter.getStyleClass().add("count");

            List<Node> cardAndCountNodeList = initialiseCard();
            cardAndCount.getChildren().add(counter);
            cardAndCount.getChildren().addAll(cardAndCountNodeList);
            handCard.getChildren().add(cardAndCount);
        }
        HandView.getChildren().add(billets);
        HandView.getChildren().add(handCard);

        return HandView;
    }

    public static Node createCardsView(ObservableGameState gameState) {// TODO PREND PLUS D'ARG


        VBox cardVue = new VBox();
        cardVue.setId("card-pane");
        cardVue.getStylesheets().add("decks.css");
        cardVue.getStylesheets().add("colors.css");

        Button ticketDeckButton = new Button("Billets");
        ticketDeckButton.getStyleClass().add("gauged");

        Group button1Jauge = new Group();
        Rectangle foregroundRect1 = new Rectangle();
        foregroundRect1.getStyleClass().add("foreground");
        foregroundRect1.setHeight(5);
        foregroundRect1.setWidth(gameState.getPercentTicketsRemainingInDeck() / 2);

        Rectangle backgroundRect1 = new Rectangle();
        backgroundRect1.getStyleClass().add("background");
        backgroundRect1.setWidth(50);
        backgroundRect1.setHeight(5);

        button1Jauge.getChildren().add(backgroundRect1);
        button1Jauge.getChildren().add(foregroundRect1);

        ticketDeckButton.setGraphic(button1Jauge);

        Button cardDeckButton = new Button("Cartes");
        cardDeckButton.getStyleClass().add("gauged");

        Group button2Jauge = new Group();
        Rectangle foregroundRect2 = new Rectangle();
        foregroundRect2.getStyleClass().add("foreground");
        foregroundRect2.setHeight(5);
        foregroundRect2.setWidth(gameState.getPercentCardsRemainingInDeck() / 2);

        Rectangle backgroundRect2 = new Rectangle();
        backgroundRect2.getStyleClass().add("background");
        backgroundRect2.setWidth(50);
        backgroundRect2.setHeight(5);

        button2Jauge.getChildren().add(backgroundRect2);
        button2Jauge.getChildren().add(foregroundRect2);


        cardDeckButton.setGraphic(button2Jauge);

        cardVue.getChildren().add(ticketDeckButton);
        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            Card c = gameState.getFaceUpCard(i);
            StackPane card = new StackPane();
            if (c.equals(Card.LOCOMOTIVE)) {
                card.getStyleClass().add("NEUTRAL");
            } else {
                card.getStyleClass().add(c.name());
            }
            card.getStyleClass().add("card");
            List<Node> cardNodeList = initialiseCard();
            card.getChildren().addAll(cardNodeList);
            cardVue.getChildren().add(card);
        }
        cardVue.getChildren().add(cardDeckButton);
        return cardVue;
    }

    private static List<Node> initialiseCard() {
        List<Rectangle> list = new LinkedList<>();

        Rectangle rectangleOutside = new Rectangle();
        rectangleOutside.getStyleClass().add("outside");

        rectangleOutside.setWidth(60);
        rectangleOutside.setHeight(90);

        Rectangle rectangleInside = new Rectangle();
        rectangleInside.getStyleClass().add("filled");

        rectangleInside.setWidth(40);
        rectangleInside.setHeight(70);

        Rectangle rectangleImage = new Rectangle();
        rectangleImage.getStyleClass().add("train-image");
        rectangleImage.setWidth(40);
        rectangleImage.setHeight(70);

        list.add(rectangleOutside);
        list.add(rectangleInside);
        list.add(rectangleImage);

        return Collections.unmodifiableList(list);
    }
}
