package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerIdTest {

    @Test
    void nextTest() {
        assertEquals(PlayerId.PLAYER_1, PlayerId.PLAYER_2.next());
    }
}
