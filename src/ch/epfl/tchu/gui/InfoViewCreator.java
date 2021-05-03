package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

abstract class InfoViewCreator {

    public static Node createInfoView(PlayerId id, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos) {


        // The player-stats vBox
        VBox playerStatsVBox = new VBox();
        playerStatsVBox.setId("player-stats");
       for(PlayerId i : List.of(id,id.next())) {
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");
            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,playerNames.get(i),
                    gameState.numberOfTicketsOnHandProperty(i), gameState.numberOfCardsOnHandProperty(i),
                    gameState.numberOfCarsOnHandProperty(i),gameState.numberOfBuildingPointsOnHandProperty(i)));

            TextFlow playerTextFlow = new TextFlow(circle, text);
            playerTextFlow.getStyleClass().add(i.name());
            playerStatsVBox.getChildren().add(playerTextFlow);
        };

        // The separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        // The textFlow
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info"); //TODO verifier les 5 messages ?
        Bindings.bindContent(textFlow.getChildren(),gameInfos);

        // The vBox
        VBox infoView = new VBox(playerStatsVBox,separator,textFlow);
        infoView.setId("info-view");
        infoView.getStylesheets().add("info.css");
        infoView.getStylesheets().add("colors.css");

        return infoView;
    }


}
