package com.danish.world.dm;

import com.danish.world.dm.utils.CSVWriter;
import com.danish.world.dm.utils.HaversineDistance;
import smile.clustering.DBSCAN;
import smile.data.AttributeDataset;
import smile.data.parser.DelimitedTextParser;
import smile.neighbor.CoverTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBScanSmile
{
    static int nRecords = 400000;
    static String outputFile = "/mnt/1717A37A1971CE02/WorkSpaces/BDMA/data-mining/bdma-data-mining/db-scan-lab/data/output_dbscan.csv"+nRecords;
    static String inputFile = "/mnt/1717A37A1971CE02/WorkSpaces/BDMA/data-mining/DataMining_BDMA/input_data/accidents_2012_to_2014.csv"+nRecords;//464697

    static int minPts = 300;
    static double minPtsPercent = 0.003;

    static double range = 3000;

    public static void main(String[] args) throws IOException
    {
        long clock = System.currentTimeMillis();

        double [][] dataset = null;
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setIgnoredColumns(Arrays.asList(0));
        parser.setDelimiter("[, ]+");
        try {
            AttributeDataset data = parser.parse("id_long_lat", new FileInputStream(new File(inputFile)));
            dataset = data.toArray(new double[data.size()][]);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        //DBSCAN<double[]> dbscan = new DBSCAN<>(dataset, new HavesineDistance(), minPts, range);
        DBSCAN<double[]> dbscan = new DBSCAN<>(dataset, new CoverTree<>(dataset, new HaversineDistance()), (int)(dataset.length * minPtsPercent), range);
        List<List<String>> csvWritableData = new ArrayList<>(dataset.length);

        System.out.format("DBSCAN clusterings %d samples in %dms\n", dataset.length, System.currentTimeMillis()-clock);
        System.out.println(dbscan.toString());


        for (int k = 0; k < dbscan.getNumClusters(); k++) {

            double[][] cluster = new double[dbscan.getClusterSize()[k]][];
            for (int i = 0, j = 0; i < dataset.length; i++) {
                if (dbscan.getClusterLabel()[i] == k) {
                    cluster[j++] = dataset[i];
                }
            }

            for (int i = 0; i < cluster.length; i++)
            {
                csvWritableData.add(getCsvPoint(cluster[i], String.valueOf(k)));
            }

        }

        try
        {
            CSVWriter.writeCsv(outputFile, csvWritableData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static List<String> getCsvPoint(double[] point, String label)
    {
        return Arrays.asList(String.valueOf(point[0]), String.valueOf(point[1]), label);
    }
}
