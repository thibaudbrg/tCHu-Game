package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


/**
 * Represents a partition of Stations
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public final class StationPartition implements StationConnectivity {
    private final Integer[] arrayStationLink;

    private StationPartition(Integer[] arrayStationLink) {
        this.arrayStationLink = arrayStationLink.clone();
    }


    /**
     * Gives a boolean of if two stations are connected with card to each other
     *
     * @param s1 (Station) Station 1
     * @param s2 (Station) Station 2
     * @return (boolean) If two stations are connected with card to each other
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= arrayStationLink.length || s2.id() >= arrayStationLink.length) {
            return s1.id() == s2.id();
        } else return arrayStationLink[s1.id()].equals(arrayStationLink[s2.id()]);
    }


    /**
     * The Builder of StationPartition
     */
    public final static class Builder {
        private final Integer[] builderStationLink;

        /**
         * The constructor of the Builder of StationPartition
         *
         * @param stationCount number of stations that is given
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            builderStationLink = new Integer[stationCount];

            for (int i = 0; i < builderStationLink.length; i++) {
                builderStationLink[i] = i;
            }
        }

        /**
         * Joins the subsets containing the two stations passed as arguments, by "electing" one of
         * the two representatives as representative of the joined subset
         *
         * @param station1 Station 1
         * @param station2 Station 2
         * @return (Builder) The builder (this)
         */
        public Builder connect(Station station1, Station station2) {
            builderStationLink[representative(station1.id())] = representative(station2.id());
            return this;
        }

        /**
         * Gives the flattened partition of the stations corresponding to the deep partition
         * being built by this builder
         *
         * @return (StationPartition) the flattened partition of the stations corresponding to the deep partition
         * being built by this builder
         */
        public StationPartition build() {
            int i = 0;
            for (Integer representativeId : builderStationLink) {
                builderStationLink[i] = representative(representativeId);
                i++;
            }
            return new StationPartition(builderStationLink);
        }


        private int representative(int stationId) {
            int intermediateStationId = stationId;
            do {
                intermediateStationId = builderStationLink[intermediateStationId];
            } while (intermediateStationId != builderStationLink[intermediateStationId]);
            return intermediateStationId;
        }
    }
}

