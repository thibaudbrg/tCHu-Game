package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.StringJoiner;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

class MapViewCreator {
    private ObservableGameState observableGameState;
    private ObjectProperty<ClaimRouteHandler> objectProperty;
    private CardChooser cardChooser;

    public createMapView(ObservableGameState observableGameState, ObjectProperty<ClaimRouteHandler> objectProperty, CardChooser cardChooser) {
        this.observableGameState = observableGameState;
        this.objectProperty = objectProperty;
        this.cardChooser = cardChooser;

    }


    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }

    private Pane sceneGraphConstructor() {
        Pane Carte = new Pane();
        Carte.getStylesheets().add(new String("map.css"));
        Carte.getStylesheets().add(new String("color.css"));
        ImageView fond = new ImageView("map.png");
        Carte.getChildren().add((fond));
        for (Route r : ChMap.routes()) {
            Group Route = new Group();

            Route.setId(r.id());

            List<String> styleClass = List.of("route", "UNDERGROUND", "NEUTRAL");
            Route.getStyleClass().addAll(styleClass);

            for (int i = 0; i < r.length(); i++) {
                Group Case = new Group();
                Case.setId(r.id() + "_" + i);

                Rectangle Voie = new Rectangle();
                List<String> styleClassR = List.of("track", "filled");
                Voie.getStyleClass().addAll(styleClassR);
                Voie.setHeight(12);
                Voie.setWidth(36);

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

                List<Node> nodeListOfCase = List.of(Voie, Wagon);
                Case.getChildren().addAll(nodeListOfCase);
                Route.getChildren().add(Case);

            }
            Carte.getChildren().add(Route);


        }


    }
}
