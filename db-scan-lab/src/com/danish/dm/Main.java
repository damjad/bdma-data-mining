package com.danish.dm;

import com.danish.dm.algo.clustering.DBScan;
import com.danish.dm.algo.clustering.PartitionedDBScan;
import com.danish.dm.utils.CSVReader;
import com.danish.dm.utils.DistanceFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static com.danish.dm.utils.Constants.*;

public class Main
{

    public static Properties SYSTEM_PROPERTIES;
    public static Integer COUNT =0;
    public static Integer CACHE_MISS =0;


    private static File propertiesFile;

    public static void main(String[] args) throws IOException
    {
        validateArgs(args);
        SYSTEM_PROPERTIES = new Properties();
        SYSTEM_PROPERTIES.load(new FileInputStream(propertiesFile));

        List<String[]> dataSet = CSVReader.readCsv(new File(SYSTEM_PROPERTIES.getProperty(DATA_SET_FILE)));

        if ("Partitioned".equals(SYSTEM_PROPERTIES.getProperty("db-scan.algorithm")))
        {
            // Partitioned dbScam
            PartitionedDBScan partitionedDBSCan = new PartitionedDBScan(dataSet, SYSTEM_PROPERTIES);
            partitionedDBSCan.train();
            partitionedDBSCan.writeOutput();
        }
        else
        {
            DBScan dBScan = new DBScan(dataSet, SYSTEM_PROPERTIES);
            dBScan.train();
            dBScan.writeOutput();
        }
    }

    public static void validateArgs(String[] args)
    {
        boolean exitWithFailure = false;
        if (args.length == 1 && "-h".equals(args[0]))
        {
            System.out.println("First argument is the path of property file");
        }
        else if (args.length < 1)
        {
            System.err.println("Not enough arguments.\n" +
                    "First argument is the path of property file\n");
            exitWithFailure = true;
        }
        else
        {
            propertiesFile = new File(args[0]);
            if (!(propertiesFile.exists() && propertiesFile.canRead()))
            {
                System.err.println("Please enter a valid path of a file or check file permissions.");
                exitWithFailure = true;
            }
        }
        if (exitWithFailure)
        {
            System.exit(UNSUCCESSFUL_EXIT_CODE);
        }
    }
}
