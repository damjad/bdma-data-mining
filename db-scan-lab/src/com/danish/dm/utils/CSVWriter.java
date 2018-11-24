package com.danish.dm.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter
{
    public static void writeCsv(File file, List<List<String>> data) throws IOException
    {
        if(file.exists())
        {
            file.delete();
        }
        file.createNewFile();
        try (FileWriter fw = new FileWriter(file))
        {
            for (List<String> d : data)
            {
                fw.write(String.join(",",d));
                fw.write(System.lineSeparator());
            }
        }
    }

    public static void writeCsv(String fileName, List<List<String>> data) throws IOException
    {
        writeCsv(new File(fileName), data);
    }
}
