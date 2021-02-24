package ch.epfl.tchu;

/**
 * Class that verifies certain conditions for the good of the execution of certain methods
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class Preconditions {

    private Preconditions() {}

    /**
     * Throws an exception if the given parameter is false
     *
     * @param shouldBeTrue (boolean) parameter to test
     * @throws IllegalArgumentException
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
