package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }
    public void run(){
        try (ServerSocket s0 = new ServerSocket(port))
            {
                BufferedReader r  ;
                BufferedWriter w ;
                do{
                    try (Socket s = s0.accept()) {r= new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
                        w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
                        

                    }
            }
            while(r.readLine()!=null); // la boucle est suremernt pas la

            } catch (IOException e){ throw new UncheckedIOException(e);
        }
        }

}}