package com.danish.dm.algo.clustering;

import com.danish.dm.utils.Constants;
import com.danish.dm.utils.DistanceFunctions;
import com.danish.dm.beans.*;
import com.danish.dm.utils.DistanceCache;
import com.danish.dm.beans.Grid;

import static com.danish.dm.utils.DistanceFunctions.DistanceTypes;

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

    @Override
    protected void trainInternal()
    {
        this.dataSet.forEach(p -> System.out.println(p.getData()));
        // Building the grid
        Grid grid = new Grid(this.eps, this.dataSet);

        // Detecting all core points
        for (Cell<Double> cell : grid.getCells()) {
            if (cell.getPointList().size() >= minPts) {
                cell.setContainsCore();
                cell.setAllPointsAsCore();
            }
            else {
                for (DataPoint<Double> point : cell.getPointList()) {
                    grid.rangeQuery(point, minPts, distanceFunction);
                }
            }
        }

        // Merging clusters
        int clusterCount = 1;
        boolean clusterCountUpdate = false;
        for(Cell<Double> cell : grid.getCells()) {
            for(DataPoint<Double> point : cell.getPointList()) {
                clusterCountUpdate = grid.mergeCluster(clusterCount, point, distanceFunction);
                if(clusterCountUpdate)
                    clusterCount++;
            }
        }

        // detecting border and noise points
        for (Cell<Double> cell : grid.getCells()) {
            for(DataPoint<Double> point : cell.getPointList()) {
                if(!Constants.CORE.equals(point.getCalculatedLabel())) {
                    grid.lookForBorderPoints(point, distanceFunction);
                }
            }
        }

        // Printing for DEBUG
        this.printDebug();

        // Plotting

    }

    private void printDebug() {
        // Printing all points along with clusterId and label
        System.out.println("Printing all points along with clusterId and label...");
        for (DataPoint<Double> point : this.dataSet) {
            double x = point.getData().get(0);
            double y = point.getData().get(1);
            System.out.println("P(" + x + ", " + y + ")" + "-->" + "C" + point.getClusterId() + "-->" + point.getCalculatedLabel());
        }
    }
}
