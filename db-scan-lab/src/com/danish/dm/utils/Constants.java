package com.danish.dm.utils;

final public class Constants
{
    public static final int UNSUCCESSFUL_EXIT_CODE = 1;
    public static final String CSV_DELIMMETER = System.getProperty("csv.delim") == null ? "," : System.getProperty("csv.delim");
    public static final String NOISE = "Noise";

    // System property keys
    public static final String DB_SCAN_MIN_PTS = "db-scan.min-pts";
    public static final String DB_SCAN_EPS = "db-scan.eps";
    public static final String DB_SCAN_DISTANCE_TYPE = "db-scan.distance-type";
    public static final String DB_SCAN_CACHE_DISTANCE = "db-scan.cache-distances";

    public static final String DATA_SET_ID_INDEX = "data-set.id-index";
    public static final String DATA_SET_FILE = "data-set.file";


    private Constants() throws IllegalAccessException
    {
        throw new IllegalAccessException("Illegal access to Constants()");
    }
}
