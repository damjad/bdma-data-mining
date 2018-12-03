package com.danish.dm.utils;

import com.danish.dm.beans.DataPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils
{

    public static void unSuccessfulExit(String message)
    {
        System.err.println(message);
        System.exit(Constants.UNSUCCESSFUL_EXIT_CODE);
    }

    public static <T extends Number> T parseNumber(String s, Class<T> clazz)
    {
        if (clazz.equals(Integer.class))
        {
            return (T) Integer.valueOf(s);
        }

        if (clazz.equals(Float.class))
        {
            return (T) Float.valueOf(s);
        }

        if (clazz.equals(Double.class))
        {
            return (T) Double.valueOf(s);
        }

        throw new IllegalArgumentException("Only Int, Float and Double are supported");
    }

    //
    public static <T> List<T> union(List<T> list1, List<T> list2) {

        ArrayList<T> s =  new ArrayList<T>(list1);
        s.addAll(difference(list2, list1));
        return s;
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static <T> List<T> difference(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(!list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }


    public static <T extends Number> void writeToCSV(String fileName, List<DataPoint<T>> dataSet) throws IOException
    {
        CSVWriter.writeCsv(fileName, dataSet.stream().map(Utils::getWritableDataPoint).collect(Collectors.toList()));
    }

    private static <T extends Number> List<String> getWritableDataPoint(DataPoint<T> dataPoint)
    {
        List<String> attr = new ArrayList<>();
        attr.add(dataPoint.getId());
        attr.add(dataPoint.getCalculatedLabel());
        attr.add(String.valueOf(dataPoint.getClusterId()));
        attr.addAll(dataPoint.getData().stream().map(d -> (d).toString()).collect(Collectors.toList()));

        return attr;
    }
}