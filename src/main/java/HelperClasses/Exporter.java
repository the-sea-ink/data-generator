package HelperClasses;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Exporter {



    public static FileWriter exporterInit() throws IOException {
        FileWriter csvWriter = new FileWriter("output/output.csv");
        return csvWriter;
    }

    public static void exporter(FileWriter csvWriter, String dataString) throws IOException {
        //BufferedWriter bw = new BufferedWriter(csvWriter);
        csvWriter.append(dataString);
        csvWriter.flush();
        //csvWriter.close();
    }

}
