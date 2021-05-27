package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Allows to create the map view
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
abstract class MapViewCreator {
    private final static int CIRCLE_RADIUS = 3;
    private final static int FIRST_CIRCLE_X = 12;
    private final static int CIRCLE_Y = 6;
    private final static int SECOND_CIRCLE_X = 24;
    private final static int RECTANGLE_WIDTH = 36;
    private final static int RECTANGLE_HEIGHT = 12;


    /**
     * Functional interface to choose a card
     */
    @FunctionalInterface
    interface CardChooser {

        /**
         * Called when the player has to choose the cards he wants to use to seize a road.
         * The possibilities are given by the options argument,
         * while the action handler is intended to be used when he has made his choice.
         *
         * @param options (List<SortedBag<Card>>) The options available
         * @param handler (ActionHandler.ChooseCardsHandler) The handler to be used after his choice
         */
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }

    /**
     * @param gameState    (ObservableGameState) The observable GameState
     * @param claimRouteHP (ObjectProperty<ActionHandler.ClaimRouteHandler>) Property containing the action handler
     *                     to use when the player wants to seize a road
     * @param cardChooser  (CardChooser) A Card selector
     * @return (Node) The view of the hand
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {

        Pane Carte = new Pane();
        Carte.getStylesheets().add("map.css");
        Carte.getStylesheets().add("colors.css");
        Carte.getChildren().add((new ImageView("map.png")));

        for (Route r : ChMap.routes()) {
            Group groupRoute = new Group();
            gameState.routesProperty(r).addListener((observable, oldValue, newValue) -> {
                if (oldValue == null) {
                    groupRoute.getStyleClass().add(newValue.name());
                }
            });

            groupRoute.setId(r.id());

            List<String> styleClass = List.of("route", r.level().name(), r.color() == null ? StringsFr.NEUTRAL : r.color().name());
            groupRoute.getStyleClass().addAll(styleClass);

            createRoute(r, groupRoute, gameState);

            groupRoute.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimForEachRouteProperty(r).not()));

            groupRoute.setOnMouseClicked((s) -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(r);
                ActionHandler.ClaimRouteHandler claimRouteH = claimRouteHP.get();
                if (possibleClaimCards.size() > 1) {
                    ActionHandler.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(r, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                } else {
                    claimRouteH.onClaimRoute(r, possibleClaimCards.get(0));

                }
            });

            Carte.getChildren().add(groupRoute);
        }
        return Carte;
    }

    private static void createRoute(Route r, Group group, ObservableGameState gameState) {
        BooleanProperty trueProperty = new SimpleBooleanProperty();
        trueProperty.set(true);
        for (int i = 1; i <= r.length(); i++) {
            Rectangle voie = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            List<String> styleClassR = List.of("track", "filled");
            voie.getStyleClass().addAll(styleClassR);

            Rectangle rectangle1 = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
            rectangle1.getStyleClass().add("filled");

            Circle circle1 = new Circle(FIRST_CIRCLE_X, CIRCLE_Y, CIRCLE_RADIUS);
            Circle circle2 = new Circle(SECOND_CIRCLE_X, CIRCLE_Y, CIRCLE_RADIUS);


            circle1.radiusProperty().bind(Bindings.when(gameState.longuestTrailProperty(r).isEqualTo(trueProperty)).then(4.5).otherwise(3));
            circle2.radiusProperty().bind(Bindings.when(gameState.longuestTrailProperty(r).isEqualTo(trueProperty)).then(4.5).otherwise(3));
            circle1.fillProperty().bind(Bindings.when(gameState.longuestTrailProperty(r).isEqualTo(trueProperty)).then(Color.SKYBLUE).otherwise(Color.WHITE));
            circle2.fillProperty().bind(Bindings.when(gameState.longuestTrailProperty(r).isEqualTo(trueProperty)).then(Color.SKYBLUE).otherwise(Color.WHITE));
            Group Wagon = new Group(rectangle1, circle1, circle2);
            Wagon.getStyleClass().add("car");

            Group Case = new Group(voie, Wagon);
            Case.setId(r.id() + "_" + i);
            group.getChildren().add(Case);
        }
    }


}
