package com.danish.dm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static com.danish.dm.utils.Constants.CSV_DELIMMETER;

public class CSVReader
{
    private CSVReader() throws IllegalAccessException
    {
        throw new IllegalAccessException("Illegal access to CSVReader()");
    }

    public static List<String[]> readCsv(File file) throws IOException
    {
        return Files.readAllLines(file.toPath())
                .stream()
                .map(l -> l.split(CSV_DELIMMETER))
                .collect(Collectors.toList());
    }

    public static List<String[]> readCsv(String fileName) throws IOException
    {
        return readCsv(new File(fileName));
    }
}
