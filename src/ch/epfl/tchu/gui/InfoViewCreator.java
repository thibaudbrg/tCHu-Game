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

/**
 * Enable to create the information vue
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
abstract class InfoViewCreator {
    /**
     * Create the information vue
     *
     * @param id (PlayerId) The corresponding player identity
     * @param playerNames(Map<PlayerId,String>) The associative table of players' names
     * @param gameState(ObservableGameState) The observable game state
     * @param gameInfos (ObservableList<Text>) Observable list containing information on the course of the game
     * @return (Node) The information vue
     */
    public static Node createInfoView(PlayerId id, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> gameInfos) {
        // The player-stats vBox
        VBox playerStatsVBox = new VBox();
        playerStatsVBox.setId("player-stats");
        for (PlayerId i : List.of(id, id.next())) {
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");
            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(i),
                    gameState.numberOfTicketsOnHandProperty(i), gameState.numberOfCardsOnHandProperty(i),
                    gameState.numberOfCarsOnHandProperty(i), gameState.numberOfBuildingPointsOnHandProperty(i)));

            TextFlow playerTextFlow = new TextFlow(circle, text);
            playerTextFlow.getStyleClass().add(i.name());
            playerStatsVBox.getChildren().add(playerTextFlow);
        }


       // The separator
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        // The textFlow
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        Bindings.bindContent(textFlow.getChildren(), gameInfos);

        // The vBox
        VBox infoView = new VBox(playerStatsVBox, separator, textFlow);
        infoView.setId("info-view");
        infoView.getStylesheets().add("info.css");
        infoView.getStylesheets().add("colors.css");

        return infoView;
    }


}