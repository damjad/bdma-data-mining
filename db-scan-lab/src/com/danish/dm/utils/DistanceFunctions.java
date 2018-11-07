package com.danish.dm.utils;

import com.danish.dm.beans.DataPoint;

public class DistanceFunctions
{
    public static <T extends Number> double calculateDistance(DataPoint<T> p1, DataPoint<T> p2, DistanceTypes distanceFunction)
    {
        switch (distanceFunction)
        {
            case TSS:
                return tss(p1, p2);

            case EUCLIDEAN:
            default:
                return euclidean(p1, p2);
        }
    }

    public static <T extends Number> double calculateDistance(DataPoint<T> p1, DataPoint<T> p2)
    {
        return calculateDistance(p1,p2, DistanceTypes.EUCLIDEAN);
    }

    public enum DistanceTypes
    {
        EUCLIDEAN, TSS
    }


    public static <T extends Number> double tss(DataPoint<T> p1, DataPoint<T> p2)
    {
        if(p1== null || p2 == null || p1.getData() == null || p2.getData() == null)
        {
            throw new IllegalArgumentException("Invalid data passed as parameters. Please check the data is not null");
        }

        if(p1.getData().size() != p2.getData().size())
        {
            throw new IllegalArgumentException("Invalid data! The width of data points is not same.");
        }

        Double sse = 0d;

        for (int i = 0; i < p1.getData().size(); i++)
        {
              sse += Math.pow((Double) p1.getData().get(i) - (Double) p2.getData().get(i), 2);
        }

         return sse;
    }

    public static <T extends Number> double euclidean(DataPoint<T> p1, DataPoint<T> p2)
    {
        return Math.sqrt(tss(p1,p2));
    }
}
