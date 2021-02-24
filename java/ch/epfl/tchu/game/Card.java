package ch.epfl.tchu.game;

import java.util.List;

/**
 * La description de la classe
 *
 * @author Decotignie Matthieu
 * @author Bourgeois Thibaud (324604)
 */
public enum Card {
    BLACK(Color.BLACK), VIOLET(Color.VIOLET), BLUE(Color.BLUE), GREEN(Color.GREEN), YELLOW(Color.YELLOW), ORANGE(Color.ORANGE), RED(Color.RED), WHITE(Color.WHITE), LOCOMOTIVE(null);

    private Color color;
    public final static List<Card> ALL = List.of(values());
    public final static int count = ALL.size();
    public final static List<Card> CARS = ALL.subList(0, 8);

    /**
     * La escription du constructeur
     *
     * @param color
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * La description de la méthode
     *
     * @param color
     * @return
     */
    public static Card of(Color color) {
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
            default:
                return LOCOMOTIVE;

        }
    }

    public Color color() {
        return color;
    }
}
