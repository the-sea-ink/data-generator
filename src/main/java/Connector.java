package HelperClasses;
import sun.awt.datatransfer.DataTransferer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Connector {

    public static void main(String[] args) throws IOException {

    }

    public static FileWriter exporterInit() throws IOException {
        FileWriter csvWriter = new FileWriter("output/output.csv");
        return csvWriter;
    }

    public static void exporter(FileWriter csvWriter, String dataString) throws IOException {
        csvWriter.append(dataString);
        csvWriter.flush();
    }

    public static void closeFile (FileWriter csvWriter) throws IOException {
        csvWriter.flush();
    }

    public static void sortByProcessingTime (int column, String outputFile) throws IOException {
        

    }

}
