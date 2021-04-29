package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

class MapViewCreator {

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }


    // private ObservableGameState observableGameState;
    //  private ObjectProperty<ClaimRouteHandler> objectProperty;
    //private CardChooser cardChooser;

    public static Node createMapView(ObservableGameState observableGameState/*, ObjectProperty<ActionHandler.ClaimRouteHandler> objectProperty, CardChooser cardChooser*/) {
        //  this.observableGameState = observableGameState;
        // this.objectProperty = objectProperty;
        //  this.cardChooser = cardChooser;
        Pane Carte = new Pane();
        Carte.getStylesheets().add("map.css");
        Carte.getStylesheets().add("colors.css");

        ImageView font = new ImageView("map.png");
        Carte.getChildren().add((font));

        for (Route r : ChMap.routes()) {
            Group Route = new Group();

            Route.setId(r.id());

            List<String> styleClass = List.of("route", r.level().name(), r.color() == null ? "NEUTRAL" : r.color().name());
            Route.getStyleClass().addAll(styleClass);

            for (int i = 1; i <= r.length(); i++) {
                Group Case = new Group();
                Case.setId(r.id() + "_" + i);

                Rectangle voie = new Rectangle();
                List<String> styleClassR = List.of("track", "filled");
                voie.getStyleClass().addAll(styleClassR);
                voie.setHeight(12);
                voie.setWidth(36);

                Group Wagon = new Group();
                Wagon.getStyleClass().add("car");

                Rectangle rectangle1 = new Rectangle();
                rectangle1.getStyleClass().add("filled");

                Circle circle1 = new Circle();
                circle1.setCenterX(12);
                circle1.setCenterY(6);
                circle1.setRadius(3);

                Circle circle2 = new Circle();
                circle1.setCenterX(24);
                circle1.setCenterY(6);
                circle1.setRadius(3);


                List<Node> nodeListOfWagon = List.of(rectangle1, circle1, circle2);
                Wagon.getChildren().addAll(nodeListOfWagon);

                List<Node> nodeListOfCase = List.of(voie, Wagon);
                Case.getChildren().addAll(nodeListOfCase);
                Route.getChildren().add(Case);

            }
            Carte.getChildren().add(Route);


        }

        return Carte;
    }


  /*  @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }*/


}
