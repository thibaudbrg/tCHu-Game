package ch.epfl.tchu.game;

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
    LOCOMOTIVE(null);

    private final Color color;

    public final static List<Card> ALL = List.of(values());
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
        return switch (color) {
            case RED -> RED;
            case BLUE -> BLUE;
            case BLACK -> BLACK;
            case GREEN -> GREEN;
            case WHITE -> WHITE;
            case ORANGE -> ORANGE;
            case VIOLET -> VIOLET;
            case YELLOW -> YELLOW;
            default -> throw new Error();
        };
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
