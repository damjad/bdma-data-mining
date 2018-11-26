package com.danish.dm.beans;

import java.util.*;
import java.lang.Math;
import java.util.function.ToDoubleFunction;

import com.danish.dm.beans.Cell;
import com.danish.dm.beans.DataPoint;
import com.danish.dm.utils.Constants;
import com.danish.dm.utils.DistanceCache;
import com.danish.dm.utils.DistanceFunctions;

import javax.xml.crypto.Data;

import static com.danish.dm.Main.COUNT;

public class Grid {
    private HashMap<Integer, Cell<Double>> grid;
    private int nCols, nRows, xIndex, yIndex;
    private double xMax, xMin, yMax, yMin;
    private double eps;

    public Grid(double eps, List<DataPoint<Double>> dataSet, int xIndex, int yIndex) {
        this.grid = new HashMap<>();
        this.eps = eps;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.computeStats(dataSet);
        this.populateGrid(dataSet);
    }

    public int getCellId (DataPoint<Double> point) {
        double x = point.getData().get(xIndex);
        double y = point.getData().get(yIndex);
        double cellWidth = eps/Math.sqrt(2);
        Double i = (x-this.xMin)/cellWidth;
        Double j = (y-this.yMin)/cellWidth;
        return (i.intValue()*(this.nRows) + j.intValue());
    }

    public void insertPoint (DataPoint<Double> point) {
        int cellId = getCellId(point);
        if (this.grid.containsKey(cellId)) {
            this.grid.get(cellId).addPoint(point);
        }
        else {
            Cell c = new Cell<Double>();
            c.addPoint(point);
            this.grid.put(cellId, c);
        }
    }


    private void computeStats(List<DataPoint<Double>> dataSet) {
        double xMax, xMin;
        double yMax, yMin;

        xMax = xMin = dataSet.get(0).getData().get(xIndex);
        yMax = yMin = dataSet.get(0).getData().get(yIndex);

        for (DataPoint<Double> point : dataSet) {
            if (point.getData().get(xIndex) > xMax)
                xMax = point.getData().get(xIndex);
            if (point.getData().get(xIndex) < xMin)
                xMin = point.getData().get(xIndex);
            if (point.getData().get(yIndex) > yMax)
                yMax = point.getData().get(yIndex);
            if (point.getData().get(yIndex) < yMin)
                yMin = point.getData().get(yIndex);
        }
        this.xMax = xMax;
        this.yMax = yMax;
        this.xMin = xMin;
        this.yMin = yMin;

        double cellWidth = this.eps/Math.sqrt(2);
        this.nRows = (int)Math.ceil((this.yMax - this.yMin) / cellWidth);
        this.nCols = (int)Math.ceil((this.xMax - this.xMin) / cellWidth);
        if (this.nRows == 0)
            this.nRows = 1;
        if (this.nCols == 0)
            this.nCols = 1;
    }

    private void populateGrid (List<DataPoint<Double>> dataSet) {
        for (DataPoint<Double> point : dataSet) {
            this.insertPoint(point);
        }
    }

    public List<Cell<Double>> getCells() {
        return new ArrayList<>(this.grid.values());
    }

    public void rangeQuery(DataPoint<Double> point, int minPoints, DistanceFunctions.DistanceTypes distanceFunction) {
        // Retrieving the cell where the point is contained
        int cellId = this.getCellId(point);
        Cell<Double> currentCell = this.grid.get(cellId);

        // Initialize the counter to the number of points in the current cell
        int pointCounter = currentCell.getPointList().size();

        // Iterating on all possible cells in the neighbour, starting from the adjacent ones.
        boolean termination = false;

        Collection<Integer> neighCellsId = this.getKernel(cellId);
        for (int id : neighCellsId) {
            pointCounter += this.countNeighbourPoints(id, point, distanceFunction);
            if (pointCounter >= minPoints) {
                termination = true;
                break;
            }
        }

        // Check result
        if (termination) {
            // This point is a core point
            point.setCalculatedLabel(Constants.CORE);
            currentCell.setContainsCore();
        }
    }

    // it counts all the points in cellId closer than eps to point
    private int countNeighbourPoints (int cellId, DataPoint<Double> consideredPoint, DistanceFunctions.DistanceTypes distanceFunction) {
        if (!this.grid.containsKey(cellId))
            return 0;
        int counter = 0;
        Cell<Double> cell = this.grid.get(cellId);
        for (DataPoint<Double> point : cell.getPointList()) {
            if (this.getDistance(point, consideredPoint, distanceFunction) <= eps) {
                counter++;
            }
        }
        return counter;
    }


    public void mergeCluster(int clusterCount, Cell<Double> cell, DistanceFunctions.DistanceTypes distanceFunction) {
        int cellId = getCellId(cell.getPointList().get(0));

        // Initializing the cluster
        cell.setClusterId(clusterCount);

        // Iterating on the adjacent cells
        boolean isFound;
        int startId = cellId - this.nRows -1; // start from the bottom left cell
        for (int i=-1; i<2; i++) {
            for (int id=startId; id < startId+3; id++) {

                isFound = (id != cellId) &&
                        computeX(cellId)+i == computeX(id)  &&
                        this.grid.containsKey(id)  &&
                        this.grid.get(id).getClusterId() == -1  &&
                        this.findCloseCorePoints(id, cellId, distanceFunction);

                if (isFound) {
                    this.mergeCluster(clusterCount, this.grid.get(id), distanceFunction);
                }
            }
            startId += nRows;
        }
    }

    /*
    This method finds if two cells have at least one core point per each closer than eps
     */
    private boolean findCloseCorePoints (int cellId1, int cellId2, DistanceFunctions.DistanceTypes distanceFunction) {
        Cell<Double> cell1 = this.grid.get(cellId1);
        Cell<Double> cell2 = this.grid.get(cellId2);

        for (DataPoint<Double> point1 : cell1.getPointList()) {
            for (DataPoint<Double> point2 : cell2.getPointList()) {
                if (Constants.CORE.equals(point1.getCalculatedLabel()) &&
                        Constants.CORE.equals(point2.getCalculatedLabel()) &&
                        this.getDistance(point1, point2, distanceFunction) <= eps) {
                    return true;
                }
            }
        }
        return false;
    }



    public boolean mergeClusterKunal(int clusterCount, DataPoint<Double> point, DistanceFunctions.DistanceTypes distanceFunction) {
        int cellId = this.getCellId(point);
        Cell<Double> originalCell = this.grid.get(cellId);

        boolean isFound;
        boolean clusterCountUpdate = false;
        int startId = cellId - this.nRows - 1;
        for (int i=0; i<3; i++) {
            for (int id = startId; id < startId + 3; id++) {
                Cell<Double> neighbourCell = this.grid.get(id);
                isFound = neighbourCell != null ? this.neighbourPointDist(neighbourCell, originalCell, point, distanceFunction) : false;
                if (isFound) {
                    if (originalCell.getClusterId() == -1  && neighbourCell.getClusterId() == -1) {
                        originalCell.setClusterId(clusterCount);
                        for(DataPoint<Double> pt : originalCell.getPointList()) {
                            pt.setClusterId(clusterCount);
                        }
                        clusterCountUpdate = true;
                    }
                    neighbourCell.setClusterId(originalCell.getClusterId());
                    for(DataPoint<Double> pt : neighbourCell.getPointList()) {
                        pt.setClusterId(originalCell.getClusterId());
                    }
                }
            }
            startId+=nRows;
        }
        if(originalCell.getClusterId() == -1) {
            originalCell.setClusterId(clusterCount);
            for(DataPoint<Double> pt : originalCell.getPointList()) {
                pt.setClusterId(clusterCount);
            }
            clusterCountUpdate = true;
        }
        return clusterCountUpdate;
    }

    public void lookForBorderPoints(DataPoint<Double> point, DistanceFunctions.DistanceTypes distanceFunction) {
        DataPoint<Double> q = null;
        int cellId = this.getCellId(point);
        Double minDistance = Double.MAX_VALUE;

        int startId = cellId - this.nRows -1;
        for (int i=0; i<3; i++) {
            for (int id=startId; id < startId+3; id++) {
                Cell<Double> neighbourCell = this.grid.get(id);
                DataPoint<Double> nearestCorePoint = neighbourCell!=null ? this.findNearestCorePoint(neighbourCell, point, distanceFunction) : null;
                Double distance = nearestCorePoint!=null ? this.getDistance(nearestCorePoint, point, distanceFunction) : Double.MAX_VALUE;
                if(minDistance > distance) {
                    minDistance = distance;
                    q = nearestCorePoint;
                }
            }
            startId += nRows;
        }
        if(q!=null) {
            point.setClusterId(q.getClusterId());
            point.setCalculatedLabel(Constants.BORDER);
        } else {
            point.setCalculatedLabel(Constants.NOISE);
        }
    }

    private boolean neighbourPointDist(Cell<Double> neighborCell, Cell<Double> originalCell, DataPoint<Double> consideredPoint, DistanceFunctions.DistanceTypes distanceFunction) {
        boolean foundAnotherCorePoint = false;
        for (DataPoint<Double> point : neighborCell.getPointList()) {
            if(neighborCell!=originalCell && Constants.CORE.equals(point.getCalculatedLabel()) &&
                    Constants.CORE.equals(consideredPoint.getCalculatedLabel()) &&
                    this.getDistance(point, consideredPoint, distanceFunction) <= eps) {
                foundAnotherCorePoint = true;
                break;
            }
        }
        return foundAnotherCorePoint;
    }

    private DataPoint<Double> findNearestCorePoint(Cell<Double> neighbourCell, DataPoint<Double> point,
                                                   DistanceFunctions.DistanceTypes distanceFunction) {
        Double minDistance = Double.MAX_VALUE;
        DataPoint<Double> tempPoint = null;
        for(DataPoint<Double> pointVar : neighbourCell.getPointList()) {
            if(Constants.CORE.equals(pointVar)) {
                Double distance = this.getDistance(pointVar, point, distanceFunction);
                if(distance < minDistance) {
                    minDistance = distance;
                    tempPoint = pointVar;
                }
            }
        }
        return tempPoint;
    }

    private int computeX (int cellId) {
        return cellId/this.nRows;
    }
    private int computeY (int cellId) {
        return cellId - computeX(cellId)*this.nRows;
    }

    // It gets the 21 cells in the neighborhood of cellId
    private Collection<Integer> getKernel(int cellId) {
        Set<Integer> neighCells = new HashSet<>();

        // First cycle - Internal cells
        int startId = cellId - this.nRows -1; // start from the bottom left cell
        for (int i=-1; i<2; i++) {
            for (int id=startId; id < startId+3; id++) {
                if (computeX(cellId)+i != computeX(id))
                    continue;
                if (id != cellId)
                    neighCells.add(id);
            }
            startId += this.nRows;
        }

        // Second cycle - external cells
        startId = cellId - 2*this.nRows -1;
        for (int id=startId; id<startId+3; id++) {
            if (computeX(cellId)-2 != computeX(id))
                continue;
            neighCells.add(id);
        }
        startId = cellId + 2*this.nRows -1;
        for (int id=startId; id<startId+3; id++) {
            if (computeX(cellId)+2 != computeX(id))
                continue;
            neighCells.add(id);
        }
        startId = cellId - this.nRows +2;
        for (int id=startId; id<startId+3; id+=this.nRows) {
            if (computeY(cellId)+2 != computeY(id))
                continue;
            neighCells.add(id);
        }
        startId = cellId - this.nRows -2;
        for (int id=startId; id<startId+3; id+=this.nRows) {
            if (computeY(cellId)-2 != computeY(id))
                continue;
            neighCells.add(id);
        }

        return neighCells;
    }


    protected Double getDistance(DataPoint<Double> p1, DataPoint<Double> p2, DistanceFunctions.DistanceTypes distanceFunction)
    {
        if (p1 == p2)
            return 0.0d;

        return DistanceFunctions.calculateDistance(p1, p2, distanceFunction);
    }
}