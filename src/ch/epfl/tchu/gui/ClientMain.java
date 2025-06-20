package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Contains the main program of the tCHu client.
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class ClientMain extends Application {

    private final static int ARG_LIST_SIZE = 2;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Is responsible for starting the client in :
     * - analyzing the arguments passed to the program to determine the host name and port number of the server
     * - creating a remote client associated with a graphical player
     * - starting the thread managing the network access, which does nothing but execute the run method of the previously created client
     *
     * @param primaryStage (Stage) Not used
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> argList = getParameters().getRaw();
        int size = argList.size();
        GraphicalPlayerAdapter player = new GraphicalPlayerAdapter();
        String name = size == ARG_LIST_SIZE || size == 1 ? argList.get(0) : "localhost";
        int port = size == ARG_LIST_SIZE ? Integer.parseInt(argList.get(1)) : 5108;
        new Thread(() -> new RemotePlayerClient(player, name, port).run()).start();
    }
}
