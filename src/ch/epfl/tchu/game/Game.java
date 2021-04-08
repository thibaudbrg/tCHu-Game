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

        // Initialise playerNames
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

       // Create the gameState and inform the players of who will start playing first
        GameState gameState = GameState.initial(tickets, rng);
        sendInfoToBothPlayers(players, new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst());

        // Give to the players the five tickets that have been dealt to them
        for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
            player.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        // The chosen tickets have been added to the given player's hand
        for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
            updateStateBothPlayers(players, gameState);
            gameState = gameState.withInitiallyChosenTickets(player.getKey(), player.getValue().chooseInitialTickets());
        }

        // Informs the players how many tickets each has kept
        for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(player.getKey())).keptTickets(gameState.playerState(player.getKey()).ticketCount()));
        }

        boolean endGame = false;
        while (gameState.lastPlayer() != gameState.currentPlayerId().next() || endGame) {
            endGame = false;
            Player actualPlayer = players.get(gameState.currentPlayerId());
            String actualPlayerName = playerNames.get(gameState.currentPlayerId());
            Info currentInfoPlayer = new Info(actualPlayerName);

            sendInfoToBothPlayers(players, currentInfoPlayer.canPlay());
            updateStateBothPlayers(players, gameState);

            switch (actualPlayer.nextTurn()) {

                // If the actualPlayer wants to draw some tickets
                case DRAW_TICKETS:
                    sendInfoToBothPlayers(players, currentInfoPlayer.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> chosenTickets = actualPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    sendInfoToBothPlayers(players, currentInfoPlayer.keptTickets(chosenTickets.size()));
                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);

                    // If the actualPlayer wants to draw a card
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

                    // If the actualPlayer wants to claim a route
                case CLAIM_ROUTE:
                    Route route = actualPlayer.claimedRoute();
                    SortedBag<Card> initialClaimCards = actualPlayer.initialClaimCards();

                    // If the claim route is a tunnel
                    if (route.level().equals(Route.Level.UNDERGROUND)) {
                        sendInfoToBothPlayers(players, currentInfoPlayer.attemptsTunnelClaim(route, initialClaimCards));
                        SortedBag<Card> drawnCard = threeOnTheTopDeckCards(gameState, rng);
                        int additionalCost = route.additionalClaimCardsCount(initialClaimCards, drawnCard);
                        sendInfoToBothPlayers(players, currentInfoPlayer.drewAdditionalCards(drawnCard, additionalCost));

                        if (additionalCost >= 1) {
                            List<SortedBag<Card>> possibleAdditionalCards = gameState.currentPlayerState().possibleAdditionalCards(additionalCost, initialClaimCards, drawnCard);
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
                    }
                    // If the claim route is not a tunnel
                    else {
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                        sendInfoToBothPlayers(players, currentInfoPlayer.claimedRoute(route, initialClaimCards));
                    }

            }
            if (gameState.lastTurnBegins()) {
                endGame = true;
                sendInfoToBothPlayers(players, currentInfoPlayer.lastTurnBegins(gameState.currentPlayerState().carCount()));
            }

            gameState = gameState.forNextTurn();
        }

        Integer[] playerPoints = whichPlayerHasTheLongest(gameState, players, playerNames);
        updateStateBothPlayers(players, gameState);
        whoWonTheGame(players, playerNames, playerPoints);
    }

    /**
     * Awarding of the longest route bonus
     *
     * @param gameState   (GameState) The gameState
     * @param players     (Map<PlayerId, Player>) The players
     * @param playerNames (Map<PlayerId, String>) The playerNames
     * @return (Integer[]) The both playerPoints
     */

    private static Integer[] whichPlayerHasTheLongest(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
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
        return new Integer[]{player1Points, player2Points};
    }

    /**
     * Determine who won the game
     *
     * @param players      (Map<PlayerId, Player>) The players
     * @param playerNames  (Map<PlayerId, String>) The playerNames
     * @param playerPoints (Integer[]) The both playerPoints
     */
    private static void whoWonTheGame(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Integer[] playerPoints) {
        if (playerPoints[0] < playerPoints[1]) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_2)).won(playerPoints[1], playerPoints[0]));
        } else if (playerPoints[0] > playerPoints[1]) {
            sendInfoToBothPlayers(players, new Info(playerNames.get(PlayerId.PLAYER_1)).won(playerPoints[0], playerPoints[1]));
        } else {
            List<String> playerNamesList = new ArrayList<>();
            playerNames.forEach((k, s) -> playerNamesList.add(s));
            sendInfoToBothPlayers(players, Info.draw(playerNamesList, playerPoints[0]));
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
        players.forEach((Id, p) -> p.receiveInfo(s));
    }

    private static void updateStateBothPlayers(Map<PlayerId, Player> players, GameState newState) {
        players.forEach((Id, p) -> p.updateState(newState, newState.playerState(Id)));
    }

}
