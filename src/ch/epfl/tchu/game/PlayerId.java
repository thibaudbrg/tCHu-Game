package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the Id of a player
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public enum PlayerId {
    PLAYER_1(), PLAYER_2();

    public final static List<PlayerId> ALL = List.of(values());
    public final static int COUNT = ALL.size();

    /**
     *Returns the identity of the player who follows the one to whom it is applied
     *
     * @return (PlayerId) The identity of the player who follows the one to whom it is applied
     */
    public PlayerId next() {
        return (this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1);
    }
}
