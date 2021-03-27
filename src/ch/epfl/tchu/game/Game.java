package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game {

    static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playerNames.size() == 2);
        Preconditions.checkArgument(players.size() == 2);

        players.forEach((Id, p) -> {
            p.initPlayers(Id, playerNames);
        });

        GameState gameState = GameState.initial(tickets, rng);

        sendInfoToBothPlayers(players, new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst());

        for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
            e.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
//TODO JSP SI IL FAUT ENLEVER LES TICKETS A CHAQUE FOIS OU SI IL FAUT FAIRE QQCH
        }
        for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
          updateStateBothPlayers(players,gameState);
            gameState = gameState.withInitiallyChosenTickets(e.getKey(), e.getValue().chooseInitialTickets());
        }
        for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(e.getKey())).keptTickets(gameState.playerState(e.getKey()).ticketCount()));
        }

        do {
            Player actualPlayer = players.get(gameState.currentPlayerId());
            String actualPlayerName = playerNames.get(gameState.currentPlayerId());
            PlayerState actualPlayerState = gameState.currentPlayerState();
            Info currentInfoPlayer = new Info(actualPlayerName);

            sendInfoToBothPlayers(players, currentInfoPlayer.canPlay());
            updateStateBothPlayers(players, gameState);

            switch (players.get(gameState.currentPlayerId()).nextTurn()) {
                case DRAW_TICKETS:
                    sendInfoToBothPlayers(players, currentInfoPlayer.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    SortedBag choosenTickets = actualPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    sendInfoToBothPlayers(players, currentInfoPlayer.keptTickets(choosenTickets.size()));

                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), choosenTickets).forNextTurn();

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int slot = actualPlayer.drawSlot();
                        if (slot == Constants.DECK_SLOT) {
                            sendInfoToBothPlayers(players, currentInfoPlayer.drewBlindCard());
                            gameState = gameState.withBlindlyDrawnCard().forNextTurn();
                        } else {
                            sendInfoToBothPlayers(players, currentInfoPlayer.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                            gameState = gameState.withDrawnFaceUpCard(slot).forNextTurn();
                        }
                       if(i==0) updateStateBothPlayers(players, gameState);
                    }

                case CLAIM_ROUTE:
                    if (
                            actualPlayer.claimedRoute().level().equals(Route.Level.UNDERGROUND) &&
                                    actualPlayer.claimedRoute().additionalClaimCardsCount(actualPlayer.initialClaimCards(), threeOnTheTopDeckCards(gameState, rng)) >= 1 &&
                                    actualPlayerState.canClaimRoute(actualPlayer.claimedRoute())) {
                        actualPlayer.chooseAdditionalCards(actualPlayerState.possibleClaimCards(actualPlayer.claimedRoute()));
                    }
            }
if (gameState.lastTurnBegins()) sendInfoToBothPlayers(players,currentInfoPlayer.lastTurnBegins(actualPlayerState.carCount()));
        } while (gameState.lastPlayer() == null);


    }


    private static SortedBag<Card> threeOnTheTopDeckCards(GameState gameState, Random rng) {
        SortedBag.Builder builder = new SortedBag.Builder<>();
        for (int i = 0; i < 3; ++i) {
            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
            builder.add(gameState.topCard());
            gameState = gameState.withoutTopCard();

        }
        return builder.build();

    }


    private static void sendInfoToBothPlayers(Map<PlayerId, Player> players, String s) {
        players.forEach((Id, p) -> {
            p.receiveInfo(s);
        });
    }

    private static void updateStateBothPlayers(Map<PlayerId, Player> players, GameState newState) {
        players.forEach((Id, p) -> {
            p.updateState(newState, newState.playerState(Id));
        });
    }

}
