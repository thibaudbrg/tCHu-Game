package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.event.EventHandler;


/**
 * Contains five nested functional interfaces representing different action handlers
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public interface ActionHandler extends EventHandler {


    /**
     * Handler for onDrawTickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {

        /**
         * Called when the player wants to draw tickets
         */
        void onDrawTickets();
    }


    /**
     * Handler for onDrawCard
     */
    @FunctionalInterface
    interface DrawCardHandler {

        /**
         * Called when the player wishes to draw a card from the given location
         *
         * @param slot (int) The location number
         */
        void onDrawCard(int slot);
    }


    /**
     * Handler for onClaimRoute
     */
    @FunctionalInterface
    interface ClaimRouteHandler {

        /**
         * Called when the player wants to take the given route with the given (initial) cards
         *
         * @param route (Route) The given Route
         * @param cards (Card) The initial Cards
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }


    /**
     * Handler for onChooseTickets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {

        /**
         * Called when the player has chosen to keep the donated tickets following a ticket draw
         *
         * @param tickets (SortedBag<Ticket>) The given tickets
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }


    /**
     * Handler for onChooseCards
     */
    @FunctionalInterface
    interface ChooseCardsHandler {

        /**
         * Called when the player has chosen to use the given cards as initial
         * or additional cards when taking possession of a Route. if they are additional cards,
         * then the SortedBag can be empty, which means that the player gives up on taking the tunnel
         *
         * @param cards (Card) The cards (initialCards or additionalCards)
         */
        void onChooseCards(SortedBag<Card> cards);
    }


}
