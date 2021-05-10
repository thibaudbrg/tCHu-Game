package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ClientMain extends Application {

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> argList = getParameters().getRaw();
        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        String name = argList.size() == 2 ? argList.get(0) : "localhost";
        int port = argList.size() == 2 ? Integer.parseInt(argList.get(1)) : 5108;
        RemotePlayerClient client = new RemotePlayerClient(player, "localhost", 5108);
        client.run();

    }
}
