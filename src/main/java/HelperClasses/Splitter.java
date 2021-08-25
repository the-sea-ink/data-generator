package HelperClasses;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Splitter {

    public static void split () throws IOException, ParseException {
        int sourcesAmount = ConfigReader.getAmountOfSources();
        int currentSource;

        BufferedReader br = new BufferedReader(new FileReader(ConfigReader.getOutputFile()));
        String line = "";


        BufferedReader buffreader = new BufferedReader(new FileReader(ConfigReader.getOutputFile()));
        //skip header row
        buffreader.readLine();
        List<FileWriter> writers = new ArrayList< FileWriter >();
        for (currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            writers.add(new FileWriter("output/output" + currentSource + ".csv"));
        }
        while ((line = buffreader.readLine()) != null) {
            String[] lineArray = line.split(",");
            int id = Integer.parseInt(lineArray[3]);
            line = line + System.lineSeparator();
            Connector.exporter(writers.get(id-1), line);
        }
        for (FileWriter writer : writers) {
            writer.flush();
            writer.close();
        }
    }

}
