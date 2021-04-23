package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a remote player proxy
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class RemotePlayerProxy implements Player {
    private final Socket socket;

    private final Serde<PlayerId> playerIdSerde = Serdes.PLAYER_ID_SERDE;
    private final Serde<List<String>> stringListSerde = Serdes.LIST_STRING_SERDE;
    private final Serde<String> stringSerde = Serdes.STRING_SERDE;
    private final Serde<PublicGameState> publicGameStateSerde = Serdes.PUBLIC_GAME_STATE_SERDE;
    private final Serde<PlayerState> playerStateSerde = Serdes.PLAYER_STATE_SERDE;
    private final Serde<SortedBag<Ticket>> ticketSortedBagSerde = Serdes.SORTEDBAG_TICKET_SERDE;
    private final Serde<TurnKind> turnKindSerde = Serdes.TURN_KIND_SERDE;
    private final Serde<Integer> integerSerde = Serdes.INTEGER_SERDE;
    private final Serde<Route> routeSerde = Serdes.ROUTE_SERDE;
    private final Serde<SortedBag<Card>> cardSortedBagSerde = Serdes.SORTEDBAG_CARD_SERDE;
    private final Serde<List<SortedBag<Card>>> cardSortedBagListSerde = Serdes.LIST_SORTEDBAG_CARD_SERDE;

    /**
     * Constructs a new remote player proxy
     *
     * @param socket (Socket) The socket that the proxy uses to communicate through the network
     *               with the client by exchanging text messages
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }

    /**
     * Called at the beginning of the game to give the player his ownId identity,
     * as well as the names of the different players, including his own, which are in playerNames
     *
     * @param ownId       (PlayerId) The own identity to the player
     * @param playerNames (Map<PlayerId, String>) The names of the players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> serializedArgument = new LinkedList<>();
        serializedArgument.add(playerIdSerde.serialize(ownId));
        serializedArgument.add(stringListSerde.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2))));

        sendMessage(MessageId.INIT_PLAYERS, String.join(" ", serializedArgument));
    }

    /**
     * Called each time a piece of information must be communicated to the player during the game;
     * this information is given in the form of a character string, usually produced by the Info
     *
     * @param info (String) The information
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO, stringSerde.serialize(info));
    }

    /**
     * Called whenever the state of the game has changed, to inform the player of the public component of this new state,
     * newState, as well as its own state, ownState
     *
     * @param newState (PublicGameState) The new state
     * @param ownState (PlayerState) The own state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        List<String> serializedArgument = List.of(publicGameStateSerde.serialize(newState), playerStateSerde.serialize(ownState));
        sendMessage(MessageId.UPDATE_STATE, String.join(" ", serializedArgument));
    }

    /**
     * Called at the beginning of the game to give the player the five tickets that have been dealt to him
     *
     * @param tickets (SortedBag<Ticket>) The tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(MessageId.SET_INITIAL_TICKETS, ticketSortedBagSerde.serialize(tickets));
    }

    /**
     * Called at the beginning of the game to ask the player which of the tickets they were initially dealt to keep
     *
     * @return (SortedBag < Ticket >) The tickets that the player keeps
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS, "");
        return ticketSortedBagSerde.deserialize(receiveMessage());
    }

    /**
     * Called at the beginning of a player's turn, to find out what type of action they wish to perform during that turn
     *
     * @return (TurnKind) The type of action the player wishes to perform
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN, "");
        return turnKindSerde.deserialize(receiveMessage());
    }

    /**
     * Called when the player has decided to draw additional tickets during the game,
     * in order to inform him of the tickets drawn and which ones he keeps
     *
     * @param options (SortedBad<Ticket>) the option tickets
     * @return (SortedBag < Ticket >) chosen Tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, ticketSortedBagSerde.serialize(options));
        return ticketSortedBagSerde.deserialize(receiveMessage());
    }

    /**
     * Called when the player has decided to draw railcar/locomotive cards,
     * so that he knows where he wants to draw them from: from one of the slots containing a face-up card or the deck
     *
     * @return (int) The value is between 0 and 4 if it comes from a slot containin a face-up card,
     * or the dummy slot number designating the deck of cards
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT, "");
        return integerSerde.deserialize(receiveMessage());
    }

    /**
     * Called when the player has decided to (try to) seize a road, in order to know which road it is
     *
     * @return (Route) The Route the player wants to seize
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE, "");
        return routeSerde.deserialize(receiveMessage());
    }

    /**
     * Called when the player has decided to (try to) seize a road,
     * in order to know which card(s) he initially wants to use for this
     *
     * @return (SortedBag < Card >) The Cards he initially wants to use
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS, "");
        return cardSortedBagSerde.deserialize(receiveMessage());
    }

    /**
     * Called when the player has decided to try to claim a tunnel and additional cards are needed,
     * in order to know which card(s) he wants to use for this, the possibilities being passed to him as an argument;
     * if the returned multiset is empty, it means that the player does not want to (or cannot) choose one of these possibilities
     *
     * @param options (List<SortedBag<Card>>) The possibilities to claim a tunnel
     * @return (SortedBag < Card >) The cards the player choose
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, cardSortedBagListSerde.serialize(options));
        return cardSortedBagSerde.deserialize(receiveMessage());
    }


    private void sendMessage(MessageId messageId, String serialized) {
        try {BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
            w.write(messageId.name());
            w.write(" " + serialized);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private String receiveMessage() {
        try {BufferedReader r = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
