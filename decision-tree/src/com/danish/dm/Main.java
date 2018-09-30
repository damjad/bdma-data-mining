package com.danish.dm;

import com.danish.dm.algo.DecisionTree;
import com.danish.dm.utils.CSVReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.danish.dm.utils.Constants.UNSUCCESSFUL_EXIT_CODE;

public class Main
{

    private static Integer outputIndex;
    private static File inputFile;

    public static void main(String[] args) throws IOException
    {
        validateArgs(args);
        List<String[]> dataSet = CSVReader.readCsv(inputFile);
        DecisionTree decisionTree = new DecisionTree(dataSet, outputIndex);

        decisionTree.train();

        decisionTree.display();

    }

    public static void validateArgs(String[] args)
    {
        boolean exitWithFailure = false;
        if (args.length == 1 && "-h".equals(args[0]))
        {
            System.out.println("First argument is the index (>=0)\n" +
                    "Second argument is the path of the file");
        }
        else if (args.length < 2)
        {
            System.err.println("Not enough arguments.\n" +
                    "First argument is the index (>=0)\n" +
                    "Second argument is the path of the file");
            exitWithFailure = true;
        }
        else
        {
            try
            {
                outputIndex = Integer.parseInt(args[0]);
                if (outputIndex < 0)
                {
                    System.err.println("Please enter a valid index (>=0)");
                    exitWithFailure = true;

                }
            }
            catch (NumberFormatException ex)
            {
                System.err.println("Please enter a valid index (>=0)");
                exitWithFailure = true;
            }

            inputFile = new File(args[1]);
            if (!(inputFile.exists() && inputFile.canRead()))
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
