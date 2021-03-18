package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationPartitionTest {
    @Test
    void StationPartitionWorks() {
        StationPartition.Builder builder = new StationPartition.Builder(15);
        builder.connect(ChMap.stations().get(0), ChMap.stations().get(1)).connect(ChMap.stations().get(1), ChMap.stations().get(2))
                .connect(ChMap.stations().get(12), ChMap.stations().get(13)).connect(ChMap.stations().get(13), ChMap.stations().get(7))
                .connect(ChMap.stations().get(5), ChMap.stations().get(14));
        StationPartition s = builder.build();
        assertEquals(true, s.connected(ChMap.stations().get(0), ChMap.stations().get(2)));


    }

}