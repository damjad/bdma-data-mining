package com.danish.dm.algo;

import com.danish.dm.Main;
import com.danish.dm.beans.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.danish.dm.utils.Constants.UNSUCCESSFUL_EXIT_CODE;
import static com.danish.dm.utils.Utils.frequencyTable;
import static com.danish.dm.utils.Utils.unSuccessfulExit;

public class DecisionTree
{
    private List<String[]> dataSet;
    private int outputIndex;
    private Node<String> tree;

    private DecisionTreeAlgorithm algorithm = DecisionTreeAlgorithm.ID3;

    public DecisionTree(List<String[]> dataSet, int outputIndex)
    {
        this.dataSet = dataSet;
        this.outputIndex = outputIndex;
    }

    public DecisionTree(List<String[]> dataSet, int outputIndex, DecisionTreeAlgorithm algorithm)
    {
        this.dataSet = dataSet;
        this.outputIndex = outputIndex;
        this.algorithm = algorithm;
    }

    public void train()
    {
        preTrainingValidation();
        // for the first time add all the indices.
        List<Integer> attributeIndices = IntStream.range(0, dataSet.get(0).length).
                filter(g-> g!=outputIndex).boxed().collect(Collectors.toList());

        //In future if more than one type is specified, use the switch to distinguish the training method
        tree = ID3.train(dataSet, outputIndex, attributeIndices);

    }

    private void preTrainingValidation()
    {
        if(dataSet.size() <=0)
        {
            unSuccessfulExit("No data is present in the file! Please provide a valid CSV");
        }

        if(dataSet.get(0).length <= 1)
        {
            unSuccessfulExit("Only one column is present. Can't distinguish response and input variables");
        }

        if(outputIndex >= dataSet.get(0).length || outputIndex <0)
        {
            unSuccessfulExit("Output/Class Index is not invalid. It should be (>=0 and < total number of columns)");
        }
    }

    public void display()
    {
        tree.display();
    }

    private static class ID3
    {
        static Node<String> train(List<String[]> dataSet, int outputIndex, List<Integer> attributeIndices)
        {
            if (null == dataSet || dataSet.isEmpty())
            {
                return new Node<>("Failure");
            }

            Map<String, Integer> classFrequencyTable = frequencyTable(dataSet, outputIndex);

            if (classFrequencyTable.keySet().size() == 1)
            {
                return new Node<>(classFrequencyTable.keySet().iterator().next());
            }

            if (attributeIndices ==  null || attributeIndices.isEmpty())
            {
                return new Node<>(Collections.max(classFrequencyTable.entrySet(),
                        Comparator.comparingInt(Map.Entry::getValue)).getKey());
            }

            int splitIndex;
            Map<String, Integer> splitFreqTable;
            Node<String> root;

            splitIndex = getSplitIndexWithMaxGain(dataSet, attributeIndices, outputIndex);
            splitFreqTable = frequencyTable(dataSet, splitIndex);
            root = new Node<>(String.valueOf(splitIndex));

            for (Map.Entry<String, Integer> split : splitFreqTable.entrySet())
            {
                Node<String> child = train(dataSet.stream().filter(row -> split.getKey().equals(row[splitIndex])).collect(Collectors.toList()),
                                                outputIndex,
                                                attributeIndices.stream().filter( i -> splitIndex != i).collect(Collectors.toList()));

                root.addChild(split.getKey(), child);
            }

            return root;
        }


        static <T> double calculateEntropy(List<T[]> dataSet, int outputIndex)
        {
            Map<T, Integer> freqTable;

            freqTable = frequencyTable(dataSet, outputIndex);
            return calculateEntropy(freqTable);
        }

        static <T> double calculateEntropy(Map<T, Integer> freqTable)
        {
            double total;
            double entropy = 0;

            total = freqTable.values().stream().mapToInt( i -> i).sum();

            for (Map.Entry<T, Integer> entry: freqTable.entrySet())
            {
                entropy += entry.getValue() / total * Math.log(entry.getValue() / total) / Math.log(2);
            }

            return -1 * entropy;
        }

        static <T> double calculateEntropySplit(List<T[]> dataSet, int outputIndex, int splitIndex)
        {
            Map<T, Integer> splitCountMap;
            double totalItems;
            double entropySplit = 0;

            splitCountMap = frequencyTable(dataSet, splitIndex);
            totalItems = splitCountMap.values().stream().mapToInt(i -> i).sum();

            for (Map.Entry<T, Integer> classEntry : splitCountMap.entrySet())
            {
                entropySplit += (classEntry.getValue() / totalItems)
                        * calculateEntropy(dataSet.stream()
                        .filter(row -> classEntry.getKey().equals(row[splitIndex]))
                        .collect(Collectors.toList()), outputIndex);
            }

            return entropySplit;
        }

        static <T> double calculateGain(List<T[]> dataSet, int outputIndex, int splitIndex)
        {
            double entropy;
            double entropySplit;

            entropy = calculateEntropy(dataSet, outputIndex);
            entropySplit = calculateEntropySplit(dataSet, outputIndex, splitIndex);


            return entropy - entropySplit;
        }

        static <T> int getSplitIndexWithMaxGain(List<T[]> dataSet,
                                                       List<Integer> attributeIndices,
                                                       int outputIndex)
        {
            double maxGain = -1;
            int maxGainIndex =-1;
            for (Integer splitIndex : attributeIndices)
            {
                double gain = calculateGain(dataSet, outputIndex, splitIndex);
                if (gain >= maxGain)
                {
                    maxGain = gain;
                    maxGainIndex = splitIndex;
                }
            }
            return maxGainIndex;
        }

    }



    public enum DecisionTreeAlgorithm
    {
        ID3
    }
}
