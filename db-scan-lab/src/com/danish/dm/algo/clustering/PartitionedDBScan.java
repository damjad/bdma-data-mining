package com.danish.dm.algo.clustering;

import com.danish.dm.utils.DistanceFunctions;

import java.util.List;
import java.util.Properties;

public class PartitionedDBScan extends DBScan
{
    public PartitionedDBScan(List<String[]> inputData, int idIndex, int minPts, double eps, DistanceFunctions.DistanceTypes distanceFunction)
    {
        super(inputData, idIndex, minPts, eps, distanceFunction);
    }

    public PartitionedDBScan(List<String[]> dataSet, Properties properties)
    {
        super(dataSet, properties);
    }

    protected void trainInternal()
    {
        // Do partititioned stuff here.

    }
}
