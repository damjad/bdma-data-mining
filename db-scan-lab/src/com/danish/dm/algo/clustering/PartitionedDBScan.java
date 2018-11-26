package com.danish.dm.algo.clustering;

import com.danish.dm.beans.Cell;
import com.danish.dm.beans.DataPoint;
import com.danish.dm.beans.Grid;
import com.danish.dm.utils.Constants;
import com.danish.dm.utils.DistanceFunctions;

import java.util.List;
import java.util.Properties;

/**
 * An improved version of 2-d DBScan based on a master's thesis
 *
 * A faster algorithm for DBSCAN.
 * By: Ade Gunawan, A.
 */
public class PartitionedDBScan extends DBScan
{
    int xIndex = 0;
    int yIndex = 1;

    public PartitionedDBScan(List<String[]> inputData, int idIndex, int minPts, double eps, DistanceFunctions.DistanceTypes distanceFunction)
    {
        super(inputData, idIndex, minPts, eps, distanceFunction);
    }

    public PartitionedDBScan(List<String[]> dataSet, Properties properties)
    {
        super(dataSet, properties);
        this.xIndex = Integer.valueOf(properties.getProperty("pdb-scan.x_column"));
        this.yIndex = Integer.valueOf(properties.getProperty("pdb-scan.y_column"));
    }

    @Override
    protected void trainInternal()
    {
        //this.dataSet.forEach(p -> System.out.println(p.getData()));
        // Building the grid
        Grid grid = new Grid(this.eps, this.dataSet, xIndex, yIndex);

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
            if (cell.getClusterId() != -1  ||  !cell.containsCore())
                continue;

            grid.mergeCluster(clusterCount, cell, distanceFunction);
            clusterCount++;

//            for(DataPoint<Double> point : cell.getPointList()) {
//                clusterCountUpdate = grid.mergeCluster(clusterCount, point, distanceFunction);
//                if(clusterCountUpdate)
//                    clusterCount++;
//            }
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
        //this.printDebug();
        System.out.println("Total number of clusters: " + (clusterCount-1));
        this.printDebug();

        // Plotting

    }

    private void printDebug() {
        // Printing all points along with clusterId and label
        System.out.println("Printing all points along with clusterId and label...");
        for (DataPoint<Double> point : this.dataSet) {
            double x = point.getData().get(xIndex);
            double y = point.getData().get(yIndex);
            System.out.println("P(" + x + ", " + y + ")" + "-->" + "C" + point.getClusterId() + "-->" + point.getCalculatedLabel());
        }
    }
}
