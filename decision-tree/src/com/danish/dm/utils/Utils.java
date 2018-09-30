package com.danish.dm.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Utils
{
    public static <T> Map<T, Integer> frequencyTable(List<T[]> dataSet, int outputIndex)
    {
        Map<T, Integer> classFrequencyTable = new ConcurrentHashMap<>();
        //source download kro aor attach kro
        dataSet.forEach(i -> {
            classFrequencyTable.putIfAbsent(i[outputIndex], 0);
            classFrequencyTable.compute(i[outputIndex], (k, v) -> v!=null ? v + 1 : 1);
        });

        return classFrequencyTable;
    }

    public static void unSuccessfulExit(String message)
    {
        System.err.println(message);
        System.exit(Constants.UNSUCCESSFUL_EXIT_CODE);
    }


}
