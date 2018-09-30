package com.danish.dm.utils;

final public class Constants
{
    public static final int UNSUCCESSFUL_EXIT_CODE = 1;
    public static final String CSV_DELIMMETER = System.getProperty("csv.delim") == null ? "," : System.getProperty("csv.delim");


    private Constants() throws IllegalAccessException
    {
        throw new IllegalAccessException("Illegal access to Constants()");
    }
}
