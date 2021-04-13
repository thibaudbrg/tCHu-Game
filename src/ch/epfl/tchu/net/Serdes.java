package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class Serdes {

    public final static Serde<Integer> INTEGER_SERDE = Serde.of(Object::toString, Integer::valueOf);
    //TODO tester avec Charles
    public final static Serde<String> STRING_SERDE = Serde.of(s -> (Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8))),
            s -> (new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8)));

    public final static Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);
    public final static Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    public final static Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    public final static Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");
    public final static Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");
    public final static Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");
    public final static Serde<SortedBag<Ticket>> SORTEDBAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");
    /**
     *
     */
    public final static Serde<SortedBag<Card>> SORTEDBAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ";");

    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicCardState publicCardState) {
            List<String> serializedList = List.of(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards()),
                    INTEGER_SERDE.serialize(publicCardState.deckSize()),
                    INTEGER_SERDE.serialize(publicCardState.discardsSize()));
            return String.join(";", serializedList);
        }

        @Override
        public PublicCardState deserialize(String s) {
            String[] serializedArray = s.split(Pattern.quote(";"), -1);

            List<Card> faceUpCards = LIST_CARD_SERDE.deserialize(serializedArray[0]);
            int discardsSize = INTEGER_SERDE.deserialize(serializedArray[1]);
            int deckSize = INTEGER_SERDE.deserialize(serializedArray[2]);

            return new PublicCardState(faceUpCards, discardsSize, deckSize);
        }
    };


    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicPlayerState publicPlayerState) {
            List<String> serializedList = List.of(INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
                    INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
                    LIST_ROUTE_SERDE.serialize(publicPlayerState.routes()));

            return String.join(";", serializedList);
        }

        @Override
        public PublicPlayerState deserialize(String s) {
            String[] serializedArray = s.split(Pattern.quote(";"), -1);

            int ticketCount = INTEGER_SERDE.deserialize(serializedArray[0]);
            int cardCount = INTEGER_SERDE.deserialize(serializedArray[1]);
            List<Route> routesList = LIST_ROUTE_SERDE.deserialize(serializedArray[2]);

            return new PublicPlayerState(ticketCount, cardCount, routesList);


        }
    };

    public final static Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PlayerState playerState) {
            List<String> serializedList = List.of(SORTEDBAG_TICKET_SERDE.serialize(playerState.tickets()),
                    SORTEDBAG_CARD_SERDE.serialize(playerState.cards()),
                    LIST_ROUTE_SERDE.serialize(playerState.routes()));

            return String.join(";", serializedList);
        }

        @Override
        public PlayerState deserialize(String s) {
            String[] serializedArray = s.split(Pattern.quote(";"), -1);

            SortedBag<Ticket> tickets = SORTEDBAG_TICKET_SERDE.deserialize(serializedArray[0]);
            SortedBag<Card> cards = SORTEDBAG_CARD_SERDE.deserialize(serializedArray[1]);
            List<Route> routesList = LIST_ROUTE_SERDE.deserialize(serializedArray[2]);

            return new PlayerState(tickets, cards, routesList);


        }
    };

    public final static Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<>() {
        @Override
        public String serialize(PublicGameState pGS) {
            List<String> serializedList = List.of(INTEGER_SERDE.serialize(pGS.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(pGS.cardState()),
                    PLAYER_ID_SERDE.serialize(pGS.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(pGS.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(pGS.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID_SERDE.serialize(pGS.lastPlayer()));


            return String.join(":", serializedList);
        }

        @Override
        public PublicGameState deserialize(String s) {
            String[] serializedArray = s.split(Pattern.quote(":"), -1);

            int ticketsCount = INTEGER_SERDE.deserialize(serializedArray[0]);
            PublicCardState cardState = PUBLIC_CARD_STATE_SERDE.deserialize(serializedArray[1]);
            PlayerId currentPlayerId = PLAYER_ID_SERDE.deserialize(serializedArray[2]);
            PublicPlayerState player1 = PUBLIC_PLAYER_STATE_SERDE.deserialize(serializedArray[3]);
            PublicPlayerState player2 = PUBLIC_PLAYER_STATE_SERDE.deserialize(serializedArray[4]);
            Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
            PlayerId lastPlayer = PLAYER_ID_SERDE.deserialize(serializedArray[5]);

            return new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        }
    };
}