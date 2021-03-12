package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;

public final class StationPartition implements StationConnectivity {
    private Integer[] arrayStationLink;

    private StationPartition(Integer[] arrayStationLink) {
        this.arrayStationLink = arrayStationLink;
    }


    /**
     * Return if two stations are connected with card to each other
     *
     * @param s1 (Station) Station 1
     * @param s2 (Station) Station 2
     * @return
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() > arrayStationLink.length || s2.id() > arrayStationLink.length) {
            return s1.id() == s2.id();
        } else return arrayStationLink[s1.id()] == arrayStationLink[s2.id()];
    }


   public final static class Builder {
        private Integer[] builderStationLink;
        private StationPartition stationPartition;

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            builderStationLink = new Integer[stationCount];
            for (int i = 0; i < builderStationLink.length; i++) {
                builderStationLink[i] = i;
            }
        }

        public Builder connect(Station station1, Station station2) {
            builderStationLink[station1.id()] = representative(station2.id());

            return this;
        }


        public StationPartition build() {
            int i = 0;
            for (Integer representativeId: builderStationLink){
                builderStationLink[i] = representative(representativeId);
                i++;
            }

            return new StationPartition(builderStationLink);
        }

        private int representative(int stationId) {
            int intermediatStationId = stationId;
            do {
                intermediatStationId = builderStationLink[intermediatStationId];
            } while (intermediatStationId != builderStationLink[intermediatStationId]);
            return intermediatStationId;
        }
    }
}

