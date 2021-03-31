package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a game of tChu
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Game {

    /**
     * Plays a game of tCHu to the given players, whose names are listed in the playerNames table;
     * the tickets available for this game are those of tickets, and the random generator rng is used to create
     * the initial state of the game and to shuffle the cards from the discard pile into a new deck when necessary
     *
     * @param players     (Map<PlayerId, Player>) The two players
     * @param playerNames (Map<PlayerId, String>) The two playernames
     * @param tickets     (SortedBag<Ticket>) All available tickets
     * @param rng         (Random) The random generator
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
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
            updateStateBothPlayers(players, gameState);
            gameState = gameState.withInitiallyChosenTickets(e.getKey(), e.getValue().chooseInitialTickets());
        }

        for (Map.Entry<PlayerId, Player> e : players.entrySet()) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(e.getKey())).keptTickets(gameState.playerState(e.getKey()).ticketCount()));
        }

        while(gameState.lastPlayer()!= gameState.currentPlayerId()) {
            Player actualPlayer = players.get(gameState.currentPlayerId());
            String actualPlayerName = playerNames.get(gameState.currentPlayerId());
            PlayerState actualPlayerState = gameState.currentPlayerState();
            Info currentInfoPlayer = new Info(actualPlayerName);

            sendInfoToBothPlayers(players, currentInfoPlayer.canPlay());
            updateStateBothPlayers(players, gameState);

            switch (players.get(gameState.currentPlayerId()).nextTurn()) {
                case DRAW_TICKETS:
                    sendInfoToBothPlayers(players, currentInfoPlayer.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag chosenTickets = actualPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    sendInfoToBothPlayers(players, currentInfoPlayer.keptTickets(chosenTickets.size()));
                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int slot = actualPlayer.drawSlot();
                        if (slot == Constants.DECK_SLOT) {
                            sendInfoToBothPlayers(players, currentInfoPlayer.drewBlindCard());
                            gameState = gameState.withBlindlyDrawnCard();
                        } else {
                            sendInfoToBothPlayers(players, currentInfoPlayer.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }

                        if (i == 0) {
                            updateStateBothPlayers(players, gameState);
                        }
                    }

                case CLAIM_ROUTE:
                    Route route = actualPlayer.claimedRoute();
                    SortedBag<Card> initialClaimCards = actualPlayer.initialClaimCards();

                    if (route.level().equals(Route.Level.UNDERGROUND)) {
                        sendInfoToBothPlayers(players, currentInfoPlayer.attemptsTunnelClaim(route, initialClaimCards));
                        SortedBag<Card> drawnCard = threeOnTheTopDeckCards(gameState, rng);
                        int additionalCost = route.additionalClaimCardsCount(initialClaimCards, drawnCard);
                        sendInfoToBothPlayers(players, currentInfoPlayer.drewAdditionalCards(drawnCard, additionalCost));

                        if (additionalCost >= 1) {
                            List<SortedBag<Card>> possibleAdditionalCards = actualPlayerState.possibleAdditionalCards(additionalCost, initialClaimCards, drawnCard);
                            if (possibleAdditionalCards.isEmpty()) {
                                sendInfoToBothPlayers(players, currentInfoPlayer.didNotClaimRoute(route));
                            } else {
                                SortedBag<Card> theCardToTake = actualPlayer.chooseAdditionalCards(possibleAdditionalCards);
                                if (theCardToTake.isEmpty()) {
                                    sendInfoToBothPlayers(players, currentInfoPlayer.didNotClaimRoute(route));
                                } else {
                                    gameState = gameState.withClaimedRoute(route, theCardToTake);
                                    sendInfoToBothPlayers(players, currentInfoPlayer.claimedRoute(route, theCardToTake.union(initialClaimCards)));
                                }
                            }
                        } else {
                            gameState = gameState.withClaimedRoute(route, initialClaimCards);
                            sendInfoToBothPlayers(players, currentInfoPlayer.claimedRoute(route, initialClaimCards));
                        }
                        gameState = gameState.withMoreDiscardedCards(drawnCard);

                    } else {
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                        sendInfoToBothPlayers(players, currentInfoPlayer.claimedRoute(route, initialClaimCards));
                    }

            }
            if (gameState.lastTurnBegins())
                sendInfoToBothPlayers(players, currentInfoPlayer.lastTurnBegins(actualPlayerState.carCount()));

            gameState=gameState.forNextTurn();
        }


        Trail longestPlayer1Trail = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail longestPlayer2Trail = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes());
        int player1Points = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int player2Points = gameState.playerState(PlayerId.PLAYER_2).finalPoints();


        if (longestPlayer1Trail.length() < longestPlayer2Trail.length()) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_2)).getsLongestTrailBonus(longestPlayer2Trail));
            player2Points += Constants.LONGEST_TRAIL_BONUS_POINTS;

        } else if (longestPlayer1Trail.length() > longestPlayer2Trail.length()) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestPlayer1Trail));
            player1Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
        } else {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_1)).getsLongestTrailBonus(longestPlayer1Trail));
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_2)).getsLongestTrailBonus(longestPlayer2Trail));
            player1Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
            player2Points += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }


        if (player1Points < player2Points) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_1)).won(player2Points, player1Points));
        } else if (player1Points > player2Points) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_2)).won(player1Points, player2Points));
        } else {
            List<String> playerNamesList = new ArrayList<>();
            playerNames.forEach((k, s) -> {
                playerNamesList.add(s);
            });
            sendInfoToBothPlayers(players, Info.draw(playerNamesList, player1Points));
        }
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
