package ch.epfl.tchu;

/**
 * La description de la classe
 *
 * @author Decotignie Matthieu
 * @author Bourgeois Thibaud (324604)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * La description de la méthode
     *
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
