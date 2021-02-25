package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the eight colors used in the game to color the wagon and road maps
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public enum Color {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    public final static List<Color> ALL = List.of(values());
    public final static int COUNT = ALL.size();

}
