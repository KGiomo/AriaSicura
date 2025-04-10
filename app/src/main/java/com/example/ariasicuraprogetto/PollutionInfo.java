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

                @SerializedName("mainus")
                private String mainus;

                @SerializedName("ts")
                private String ts;

                @SerializedName("p1")
                private PollutantDetail p1;

                @SerializedName("p2")
                private PollutantDetail p2;

                @SerializedName("o3")
                private PollutantDetail o3;

                @SerializedName("n2")  // <-- NO₂
                private PollutantDetail n2;

                @SerializedName("s2")
                private PollutantDetail s2;

                @SerializedName("co")
                private PollutantDetail co;

                public int getAqius() {
                    return aqius;
                }

                public String getMainus() {
                    return mainus;
                }

                public String getTs() {
                    return ts;
                }

                public PollutantDetail getP1() {
                    return p1;
                }

                public PollutantDetail getP2() {
                    return p2;
                }

                public PollutantDetail getO3() {
                    return o3;
                }

                public PollutantDetail getN2() {
                    return n2;
                }

                public PollutantDetail getS2() {
                    return s2;
                }

                public PollutantDetail getCo() {
                    return co;
                }

                public static class PollutantDetail {
                    @SerializedName("conc")
                    private Double conc;

                    public Double getConc() {
                        return conc;
                    }
                }
            }
        }
    }
}