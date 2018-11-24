package com.danish.dm.algo.clustering;

import com.danish.dm.beans.DataPoint;
import com.danish.dm.utils.DistanceCache;
import com.danish.dm.utils.DistanceFunctions;
import com.danish.dm.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.danish.dm.Main.COUNT;
import static com.danish.dm.Main.SYSTEM_PROPERTIES;
import static com.danish.dm.utils.Constants.*;
import static com.danish.dm.utils.DistanceFunctions.DistanceTypes;
import static com.danish.dm.utils.Utils.unSuccessfulExit;

public class DBScan
{
    protected List<String[]> inputData;
    protected List<DataPoint<Double>> dataSet;
    protected int minPts;
    protected double eps;
    protected DistanceTypes distanceFunction;
    protected boolean useDistanceCache = false;
    protected DistanceCache distanceCache;


    public DBScan(List<String[]> inputData, int idIndex, int minPts, double eps, DistanceTypes distanceFunction)
    {
        this.inputData = inputData;
        this.dataSet = DataPoint.getDataPoints(inputData, idIndex, Double.class);
        this.minPts = minPts;
        this.eps = eps;
        this.distanceFunction = distanceFunction;
    }

    public DBScan(List<String[]> dataSet, Properties properties)
    {
        this(dataSet,
                Integer.parseInt(properties.getProperty(DATA_SET_ID_INDEX)),
                Integer.parseInt(properties.getProperty(DB_SCAN_MIN_PTS, "5")),
                Double.parseDouble(properties.getProperty(DB_SCAN_EPS, "1")),
                DistanceFunctions.DistanceTypes.valueOf(properties.getProperty(DB_SCAN_DISTANCE_TYPE,"EUCLIDEAN")));
        this.useDistanceCache = "true".equals(properties.getProperty(DB_SCAN_CACHE_DISTANCE));

    }

    public void train()
    {
        preTrainingValidation();
        trainInternal();

    }

    protected void trainInternal()
    {
        int clusterId = 0;
        for (DataPoint<Double> mDataPoint:dataSet)
        {
            if (null != mDataPoint.getCalculatedLabel())
                continue;

            List<DataPoint<Double>> mNeighbors = rangeQuery(dataSet, distanceFunction, mDataPoint, eps);

            if (mNeighbors.size() < minPts)
            {
                mDataPoint.setCalculatedLabel(NOISE);
                continue;
            }

            clusterId++;

            expandCluster(mDataPoint, clusterId, mNeighbors);

        }

    }

    private void expandCluster(DataPoint<Double> mDataPoint, int clusterId, List<DataPoint<Double>> mNeighbors)
    {
        mDataPoint.setCalculatedLabel(CORE);
        mDataPoint.setClusterId(clusterId);

        List<DataPoint<Double>> seedSet = new ArrayList<>();
        seedSet.addAll(mNeighbors);
        seedSet.remove(mDataPoint);

        DataPoint<Double> nDataPoint;
        int i = 0;
        while (i < seedSet.size())
        {
            nDataPoint = seedSet.get(i);
            ++i;

            if(NOISE.equals(nDataPoint.getCalculatedLabel()))
            {
                nDataPoint.setCalculatedLabel(BORDER);
                nDataPoint.setClusterId(clusterId);
                continue;
            }

            if(null != nDataPoint.getCalculatedLabel())
            {
                continue;
            }

            nDataPoint.setCalculatedLabel(BORDER);
            nDataPoint.setClusterId(clusterId);

            List<DataPoint<Double>> nNeighbors = rangeQuery(dataSet, distanceFunction, nDataPoint, eps);
            if (nNeighbors.size() >= minPts)
            {
                seedSet = Utils.union(seedSet, nNeighbors);
            }
        }
    }

    protected List<DataPoint<Double>> rangeQuery(List<DataPoint<Double>> dataSet, DistanceTypes distanceFunction, DataPoint<Double> dataPoint, double eps)
    {
        // in memory
        List<DataPoint<Double>> neighbours = new ArrayList<>();
        for (DataPoint<Double> qDataPoint: dataSet)
        {
            if(getDistance(dataPoint, qDataPoint, distanceFunction) <= eps)
            {
                neighbours.add(qDataPoint);
            }

        }

        return neighbours;
    }

    protected Double getDistance(DataPoint<Double> p1, DataPoint<Double> p2, DistanceTypes distanceFunction)
    {
        if (p1 == p2)
            return 0.0d;

        COUNT++;
        if (useDistanceCache)
        {
            if (null == distanceCache)
            {
                distanceCache = new DistanceCache(distanceFunction);
            }

            return distanceCache.getDistance(p1, p2);
        }

        return DistanceFunctions.calculateDistance(p1, p2, distanceFunction);
    }


    private void preTrainingValidation()
    {
        if(inputData.isEmpty())
        {
            unSuccessfulExit("No data is present in the file! Please provide a valid CSV");
        }

        if(minPts <= 1)
        {
            unSuccessfulExit("Min Points cannot be less than 2");
        }

        if(eps <=0)
        {
            unSuccessfulExit("Eps should be greater than 0.");
        }

    }

    public void calculatePerformanceStats()
    {
        // TODO:
    }

    public void display()
    {
        System.out.println("Summary: ");
        System.out.println(
                dataSet.stream().collect(
                        Collectors.groupingBy(DataPoint::getCalculatedLabel, Collectors.counting())
                ).entrySet().stream().sorted((a,b) -> a.getKey().compareTo(b.getKey())).collect(Collectors.toList())
        );

        System.out.println("\nPoints:");
        for (DataPoint<Double> dp: dataSet)
        {
            System.out.println("Id: " + dp.getId() + " label: " + dp.getCalculatedLabel());
        }
    }

    public void writeOutput(String fileName) throws IOException
    {
        Utils.writeToCSV(fileName, dataSet);
    }

    public void writeOutput() throws IOException
    {
        writeOutput(SYSTEM_PROPERTIES.getProperty(DB_SCAN_OUTPUT_FILE));
    }

    public List<String[]> getInputData()
    {
        return inputData;
    }

    public List<DataPoint<Double>> getDataSet()
    {
        return dataSet;
    }

    public int getMinPts()
    {
        return minPts;
    }

    public double getEps()
    {
        return eps;
    }

    public DistanceTypes getDistanceFunction()
    {
        return distanceFunction;
    }

    public boolean isUseDistanceCache()
    {
        return useDistanceCache;
    }

    public void setInputData(List<String[]> inputData)
    {
        this.inputData = inputData;
    }

    public void setDataSet(List<DataPoint<Double>> dataSet)
    {
        this.dataSet = dataSet;
    }

    public void setMinPts(int minPts)
    {
        this.minPts = minPts;
    }

    public void setEps(double eps)
    {
        this.eps = eps;
    }

    public void setDistanceFunction(DistanceTypes distanceFunction)
    {
        this.distanceFunction = distanceFunction;
    }

    public void setUseDistanceCache(boolean useDistanceCache)
    {
        this.useDistanceCache = useDistanceCache;
    }
}
