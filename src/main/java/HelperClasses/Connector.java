package HelperClasses;

import java.io.FileWriter;
import java.io.IOException;

public class Connector {



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

}
