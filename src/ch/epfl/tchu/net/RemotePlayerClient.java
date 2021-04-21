package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;

    private final Serde<PlayerId> playerIdSerde = Serdes.PLAYER_ID_SERDE;
    private final Serde<List<String>> stringListSerde = Serdes.LIST_STRING_SERDE;
    private final Serde<String> stringSerde = Serdes.STRING_SERDE;
    private final Serde<PublicGameState> publicGameStateSerde = Serdes.PUBLIC_GAME_STATE_SERDE;
    private final Serde<PlayerState> playerStateSerde = Serdes.PLAYER_STATE_SERDE;
    private final Serde<SortedBag<Ticket>> ticketSortedBagSerde = Serdes.SORTEDBAG_TICKET_SERDE;
    private final Serde<Player.TurnKind> turnKindSerde = Serdes.TURN_KIND_SERDE;
    private final Serde<Integer> integerSerde = Serdes.INTEGER_SERDE;
    private final Serde<Route> routeSerde = Serdes.ROUTE_SERDE;
    private final Serde<SortedBag<Card>> cardSortedBagSerde = Serdes.SORTEDBAG_CARD_SERDE;
    private final Serde<List<SortedBag<Card>>> cardSortedBagListSerde = Serdes.LIST_SORTEDBAG_CARD_SERDE;
private String line;

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    public void run() {
        try (Socket s = new Socket(name,port)) {
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
            while ((line = r.readLine())!=null){

                String[] splittedString = (line.split(Pattern.quote(" "), 0));

                switch (MessageId.valueOf(splittedString[0])) {
                    case INIT_PLAYERS:
                        PlayerId ownId = playerIdSerde.deserialize(splittedString[1]);
                        List<String> playerString = stringListSerde.deserialize(splittedString[2]);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, playerString.get(0), PlayerId.PLAYER_2, playerString.get(1));
                        player.initPlayers(ownId, playerNames);
                        break;

                    case RECEIVE_INFO:
                        String info = stringSerde.deserialize(splittedString[1]);
                        player.receiveInfo(info);
                        break;

                    case UPDATE_STATE:
                        PublicGameState newState = publicGameStateSerde.deserialize(splittedString[1]);
                        PlayerState ownState = playerStateSerde.deserialize(splittedString[2]);
                        player.updateState(newState, ownState);
                        break;

                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = ticketSortedBagSerde.deserialize(splittedString[1]);
                        player.setInitialTicketChoice(tickets);
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        String serializedTickets = ticketSortedBagSerde.serialize(player.chooseInitialTickets());
                        w.write(serializedTickets);
                        w.write('\n');
                        w.flush();
                        break;

                    case NEXT_TURN:
                        String serializedNextTurn = turnKindSerde.serialize(player.nextTurn());
                        w.write(serializedNextTurn);
                        w.write('\n');
                        w.flush();
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = ticketSortedBagSerde.deserialize(splittedString[1]);
                        String serialized = ticketSortedBagSerde.serialize(player.chooseTickets(options));
                        w.write(serialized);
                        w.write('\n');
                        w.flush();
                        break;

                    case DRAW_SLOT:
                        String serializedDrawSlot = integerSerde.serialize(player.drawSlot());
                        w.write(serializedDrawSlot);
                        w.write('\n');
                        w.flush();
                        break;

                    case ROUTE:
                        String serializedRoute = routeSerde.serialize((player.claimedRoute()));
                        w.write(serializedRoute);
                        w.write('\n');
                        w.flush();
                        break;

                    case CARDS:
                        String serializedCards = cardSortedBagSerde.serialize((player.initialClaimCards()));
                        w.write(serializedCards);
                        w.write('\n');
                        w.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> options1 = cardSortedBagListSerde.deserialize(splittedString[1]);
                        String serializedAdditionalCards = cardSortedBagSerde.serialize(player.chooseAdditionalCards(options1));
                        w.write(serializedAdditionalCards);
                        w.write('\n');
                        w.flush();
                        break;
                }
            }

        } catch (IOException e) {
            throw
                    new UncheckedIOException(e);
        }
    }

}