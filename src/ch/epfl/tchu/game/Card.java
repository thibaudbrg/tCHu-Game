package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the different types of cards in the game,
 * i.e. the eight types of wagon cards (one per suit), and the type of locomotive card
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null),
    MULTICOLOR(Color.MULTICOLOR);

    private final Color color;

    public final static List<Card> ALLEXTENDED = List.of(values());
    public final static List<Card> ALL = ALLEXTENDED.subList(0, ALLEXTENDED.size() - 1);
    public final static int COUNT = ALL.size();
    public final static List<Card> CARS = ALL.subList(0, 8);

    /**
     * Constructor which initializes the parameters
     *
     * @param color the color
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * Return the card corresponding to the input color
     *
     * @param color (Color) the color
     * @return (Card) the corresponding card
     */
    public static Card of(Color color) {
        Preconditions.checkArgument(color != null);
        switch (color) {
            case RED:
                return RED;
            case BLUE:
                return BLUE;
            case BLACK:
                return BLACK;
            case GREEN:
                return GREEN;
            case WHITE:
                return WHITE;
            case ORANGE:
                return ORANGE;
            case VIOLET:
                return VIOLET;
            case YELLOW:
                return YELLOW;
            case MULTICOLOR:
                return MULTICOLOR;
            default:
                return null;
        }
    }

    /**
     * Return the color of the card
     *
     * @return the color of the card
     */
    public Color color() {
        return color;
    }
}
