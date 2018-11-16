package com.danish.dm.beans;

import java.util.List;
import java.util.ArrayList;

import com.danish.dm.beans.DataPoint;
import com.danish.dm.utils.Constants;

public class Cell<T extends Number> {
    private List<DataPoint<T>> pointList;
    private int clusterId;
    private boolean containsCore;

    public Cell() {
        this.pointList = new ArrayList<DataPoint<T>>();
        this.clusterId = -1;
        this.containsCore = false;
    }

    public List<DataPoint<T>> getPointList() {
        return pointList;
    }

    public void addPoint(DataPoint<T> point) {
        this.pointList.add(point);
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public boolean containsCore() {
        return containsCore;
    }

    public void setContainsCore() {
        this.containsCore = true;
    }

    public void setAllPointsAsCore() {
        for (DataPoint<T> point : this.pointList) {
            point.setCalculatedLabel(Constants.CORE);
        }
    }
}