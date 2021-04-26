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

import java.util.List;


class DecksViewCreator {

    public void createHandView(GameState gameState) { //TODO changer  retour
        // Normalement pas dans la methode
        HBox HandView = new HBox();
        HandView.getStylesheets().add(" decks.css");
        HandView.getStylesheets().add("colors.css");

        ListView<Ticket> billets = new ListView<>();
        billets.setId("tickets");

        HBox handCard = new HBox();
        handCard.setId("hand-pane");
        for (Card c : Card.values()) {
            StackPane cardAndCount = new StackPane();
            if (c.equals(Card.LOCOMOTIVE)) {
                cardAndCount.getStyleClass().add("NEUTRAL");
            } else cardAndCount.getStyleClass().add(c.name());
            cardAndCount.getStyleClass().add("card");

            Text counter = new Text();
            counter.getStyleClass().add("count");

            Rectangle rectangleOutside = new Rectangle();

            rectangleOutside.setWidth(60);
            rectangleOutside.setHeight(90);

            Rectangle rectangleInside = new Rectangle();
            rectangleInside.setWidth(40);
            rectangleInside.setHeight(70);

            Rectangle rectangleImage = new Rectangle();
            rectangleImage.setWidth(40);
            rectangleImage.setHeight(70);


            List<Node> cardAndCountNodeList = List.of(counter, rectangleImage, rectangleInside, rectangleOutside);

            cardAndCount.getChildren().addAll(cardAndCountNodeList);
            handCard.getChildren().add(cardAndCount);
        }

        HandView.getChildren().add(handCard);
        HandView.getChildren().add(billets);

    }

    public void createCardsView(GameState gameState) {// TODO PREND PLUS D'ARG


        VBox cardVue = new VBox();
        cardVue.setId("card-pane");
        cardVue.getStylesheets().add("deck.css");
        cardVue.getStylesheets().add("colors.css");

        Button ticketDeckButton = new Button();
        ticketDeckButton.getStyleClass().add("gauged");

        Group button1Jauge = new Group();
        Rectangle foregroundRect1 = new Rectangle();
        foregroundRect1.setHeight(5);
        foregroundRect1.setWidth(5 * (gameState.ticketsCount() / Constants.TICKETS_COUNT));

        Rectangle backgroundRect1 = new Rectangle();
        backgroundRect1.setWidth(50);
        backgroundRect1.setHeight(5);

        button1Jauge.getChildren().add(foregroundRect1);
        button1Jauge.getChildren().add(backgroundRect1);
        ticketDeckButton.setGraphic(button1Jauge);

        Button cardDeckButton = new Button();
        cardDeckButton.getStyleClass().add("gauged");

        Group button2Jauge = new Group();
        Rectangle foregroundRect2 = new Rectangle();
        foregroundRect2.setHeight(5);
        foregroundRect2.setWidth(5 * (gameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT));

        Rectangle backgroundRect2 = new Rectangle();
        backgroundRect2.setWidth(50);
        backgroundRect2.setHeight(5);

        button2Jauge.getChildren().add(foregroundRect2);
        button2Jauge.getChildren().add(backgroundRect2);

        cardDeckButton.setGraphic(button2Jauge);
        for (Card c : gameState.cardState().faceUpCards()) {
            StackPane card = new StackPane();
            if (c.equals(Card.LOCOMOTIVE)) {
                card.getStyleClass().add("NEUTRAL");
            } else card.getStyleClass().add(c.name());
            card.getStyleClass().add("card");
            Rectangle rectangleOutside = new Rectangle();

            rectangleOutside.setWidth(60);
            rectangleOutside.setHeight(90);

            Rectangle rectangleInside = new Rectangle();
            rectangleInside.setWidth(40);
            rectangleInside.setHeight(70);

            Rectangle rectangleImage = new Rectangle();
            rectangleImage.setWidth(40);
            rectangleImage.setHeight(70);


            List<Node> cardNodeList = List.of(rectangleImage, rectangleInside, rectangleOutside);
            card.getChildren().addAll(cardNodeList);
            cardVue.getChildren().add(card);
        }

    }
}
