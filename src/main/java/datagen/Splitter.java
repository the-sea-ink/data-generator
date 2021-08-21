package datagen;

import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Splitter {

    public static void split () throws IOException, ParseException {
        int sourcesAmount = ConfigReader.getAmountOfSources();
        int currentSource;

        BufferedReader br = new BufferedReader(new FileReader(ConfigReader.getOutputFile()));
        String line = "";

        for (currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            BufferedReader buffreader = new BufferedReader(new FileReader(ConfigReader.getOutputFile()));
            //skip header row
            buffreader.readLine();
            FileWriter fileWriter = new FileWriter("output/output" + currentSource + ".csv");

            while ((line = buffreader.readLine()) != null) {
                String[] lineArray = line.split(",");
                int id = Integer.parseInt(lineArray[3]);
                if (id == currentSource+1) {
                    line = line + System.lineSeparator();
                    Connector.exporter(fileWriter, line);
                }
            }

        }

    }

}
