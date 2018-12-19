package com.danish.world.dm;

import com.danish.world.dm.utils.CSVWriter;
import com.danish.world.dm.utils.HaversineDistance;
import smile.clustering.DENCLUE;
import smile.data.AttributeDataset;
import smile.data.parser.DelimitedTextParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class DenClueSmile
{
    static HaversineDistance haversineDistance = new HaversineDistance();
    static String outputFile = "/mnt/1717A37A1971CE02/WorkSpaces/BDMA/data-mining/bdma-data-mining/db-scan-lab/data/output_denclue.csv";
    static String inputFile = "/mnt/1717A37A1971CE02/WorkSpaces/BDMA/data-mining/DataMining_BDMA/input_data/accidents_2012_to_2014.csv464697";



    static double sigma = 0.4;
    static int k = 50;
    static double distanceMeters = 8000.0d;
    static int minPts = 50;

    public static void main(String[] args) throws IOException, ParseException
    {
        double[][] dataset = readDataset();

        DENCLUE denclue = new DENCLUE(dataset, sigma, k);
        List<List<String>> csvWritableData = new ArrayList<>(dataset.length);
        Map<Integer, Boolean> attractivePointsMask = new HashMap<>();


        System.out.format("Number of clusters: %d\n", denclue.getNumClusters());
        for (int l = 0; l < denclue.getNumClusters(); l++) {
            List<double[]> clusterPoints = new ArrayList<>();
            for (int i = 0, j = 0; i < dataset.length; i++) {

                if (denclue.getClusterLabel()[i] == l && nearADensityAttractor(attractivePointsMask, dataset[i], denclue.getDensityAttractors())) {
                    clusterPoints.add(dataset[i]);
                }
            }

            if(clusterPoints.size()>= minPts)
            {
                double[][] cluster = new double[clusterPoints.size()][];
                clusterPoints.toArray(cluster);

                for (int i = 0; i < cluster.length; i++)
                {
                    csvWritableData.add(getCsvPoint(cluster[i], String.valueOf(l)));
                }
            }
        }

        List<double[]> attractiveList = new ArrayList<>();
        for (int i = 0; i < denclue.getDensityAttractors().length; i++)
        {
            if (null != attractivePointsMask.get(i) && attractivePointsMask.get(i))
            {
                attractiveList.add(denclue.getDensityAttractors()[i]);
                csvWritableData.add(getCsvPoint(denclue.getDensityAttractors()[i], "center"));
            }
        }

        double[][] attractiveArray = new double[attractiveList.size()][];
        attractiveList.toArray(attractiveArray);

        try
        {
            CSVWriter.writeCsv(outputFile, csvWritableData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static double[][] readDataset() throws IOException, ParseException
    {
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setIgnoredColumns(Arrays.asList(0));
        parser.setDelimiter("[, ]+");
        AttributeDataset data = parser.parse("id_long_lat", new FileInputStream(new File(inputFile)));
        return data.toArray(new double[data.size()][]);
    }

    private static boolean nearADensityAttractor(Map<Integer, Boolean> attractivePointsMask, double[] point, double[][] densityAttractors)
    {
        boolean yes  = false;
        for (int i = 0; i < densityAttractors.length; i++)
        {
            if (haversineDistance.d(point, densityAttractors[i]) <= distanceMeters)
            {
                yes = true;
                attractivePointsMask.putIfAbsent(i, true);
            }

        }
        return yes;
    }

    private static List<String> getCsvPoint(double[] point, String label)
    {
        return Arrays.asList(String.valueOf(point[0]), String.valueOf(point[1]), label);
    }
}
