package com.danish.world.dm.utils;

import smile.math.distance.Metric;

import java.io.Serializable;

/**
 * Implementation of Haversine distance according to:
 */
public class HaversineDistance implements Metric<double[]>, Serializable
{
    private static final long serialVersionUID = 1L;

    private int earthRadius = 6371; // Radius of the earth

    public HaversineDistance()
    {

    }

    public HaversineDistance(int earthRadius)
    {
        this.earthRadius = earthRadius;
    }

    @Override
    public double d(double[] x, double[] y)
    {

        double lon1 = x[0];
        double lon2 = y[0];
        double lat1 = x[1];
        double lat2 = y[1];

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2.0d) * Math.sin(deltaPhi / 2.0d)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(deltaLambda / 2.0d) * Math.sin(deltaLambda / 2.0d);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d - a));

        double distance = earthRadius * c * 1000.0d; // convert to meters

        if (x.length > 2 && y.length > 2)
        {
            double height = x[2] - y[2];

            distance = Math.pow(distance, 2) + Math.pow(height, 2);

            return Math.sqrt(distance);
        }

        return distance;

    }
}

