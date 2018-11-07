package com.danish.dm.utils;

import com.danish.dm.beans.DataPoint;

import java.util.concurrent.ConcurrentHashMap;
import static com.danish.dm.Main.CACHE_MISS;

public class DistanceCache
{
    final private static String KEY_FORMAT = "%s|#|%s";
    private ConcurrentHashMap<String, Double> cache = new ConcurrentHashMap();
    private DistanceFunctions.DistanceTypes distanceType;


    public DistanceCache(DistanceFunctions.DistanceTypes distanceType)
    {
        this.distanceType = distanceType;
    }


    private <T extends Number> String formatKey(DataPoint<T> p1, DataPoint<T> p2)
    {
        return String.format(KEY_FORMAT, p1.getId(), p2.getId());
    }

    public <T extends Number> double getDistance(DataPoint<T> p1, DataPoint<T> p2)
    {
        Double distance = lookup(p1, p2);
        if (null == distance)
        {
            CACHE_MISS++;
            distance = DistanceFunctions.calculateDistance(p1, p2, distanceType);
            store(p1, p2, distance);
        }

        return distance;
    }

    public <T extends Number> void store(DataPoint<T> p1, DataPoint<T> p2, double distance)
    {
        cache.put(formatKey(p1, p2), distance);
        cache.put(formatKey(p2, p1), distance);
    }

    public <T extends Number> Double lookup(DataPoint<T> p1, DataPoint<T> p2)
    {
        return cache.get(formatKey(p1,p2));
    }
}
