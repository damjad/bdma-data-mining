package com.danish.dm.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.danish.dm.utils.Utils.parseNumber;

public class DataPoint<T extends Number> implements Comparator<DataPoint<T>>
{
    private final String id;
    private List<T> data;
    private int clusterId;
    private String calculatedLabel;  // used to identify core, border and noise points


    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }

    public String getCalculatedLabel()
    {
        return calculatedLabel;
    }

    public void setCalculatedLabel(String calculatedLabel)
    {
        this.calculatedLabel = calculatedLabel;
    }

    public void setCalculatedLabel(int calculatedLabel)
    {
        this.calculatedLabel = String.valueOf(calculatedLabel);
    }

    public String getId()
    {
        return id;
    }

    public DataPoint(List<T> inputData, String id)
    {
        this.id = id;
        this.data = inputData;
    }

    public static <T extends Number> List<DataPoint<T>> getDataPoints(List<String[]> inputData, int idIndex, Class<T> clazz)
    {
        if (null == inputData)
            return new ArrayList<>();

        List<DataPoint<T>> dataSet = new ArrayList<>(inputData.size());

        for (int i = 0; i < inputData.size(); i++)
        {
            List<String> tupleAttrs = new ArrayList<>(inputData.get(i).length);
            tupleAttrs.addAll(Arrays.asList(inputData.get(i)));

            String idAttr = tupleAttrs.remove(idIndex);
            dataSet.add(new DataPoint<>(tupleAttrs.stream().map( e -> parseNumber(e, clazz)).collect(Collectors.toList()),
                    idAttr));
        }

        return dataSet;
    }


    @Override
    public int compare(DataPoint<T> obj1, DataPoint<T> obj2)
    {

        if (obj1 == obj2) {
            return 0;
        }
        if (obj1 == null) {
            return -1;
        }
        if (obj2 == null) {
            return 1;
        }
        return obj1.getCalculatedLabel().compareTo(obj2.calculatedLabel);
    }

}
