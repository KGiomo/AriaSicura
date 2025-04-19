package com.example.ariasicuraprogetto;

import com.google.gson.annotations.SerializedName;

public class PollutionInfo {

    @SerializedName("data")
    private DataDTO data;

    public DataDTO getData() {
        return data;
    }

    public static class DataDTO {
        @SerializedName("city")
        private String city;

        @SerializedName("state")
        private String state;

        @SerializedName("country")
        private String country;

        @SerializedName("current")
        private CurrentDTO current;

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getCountry() {
            return country;
        }

        public CurrentDTO getCurrent() {
            return current;
        }

        public static class CurrentDTO {
            @SerializedName("pollution")
            private PollutionDTO pollution;

            public PollutionDTO getPollution() {
                return pollution;
            }

            public static class PollutionDTO {
                @SerializedName("aqius")
                private int aqius;

                @SerializedName("ts")
                private String ts;

                @SerializedName("n2")
                private N2DTO n2;

                public int getAqius() {
                    return aqius;
                }

                public String getTs() {
                    return ts;
                }

                public N2DTO getN2() {
                    return n2;
                }

                public static class N2DTO {
                    @SerializedName("conc")
                    private int conc;

                    public int getConc(){
                        return conc;
                    }
                }
            }
        }
    }
}