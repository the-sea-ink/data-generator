package HelperClasses;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Splitter {

    public static void split () throws IOException, ParseException {
        int sourcesAmount = ConfigReader.getAmountOfSources();
        int currentSource = 0;
        int ids[] = new int[sourcesAmount];

        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        String line = "";


        for (int i = 0; i < ids.length; i++) {
            line = br.readLine();
            String[] lineArray = line.split(",");
            ids[i] = Integer.parseInt(lineArray[3]);
        }

        for (currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            BufferedReader buffreader = new BufferedReader(new FileReader("output/output.csv"));
            FileWriter fileWriter = new FileWriter("output/output" + currentSource + ".csv");

            while ((line = buffreader.readLine()) != null) {
                String[] lineArray = line.split(",");
                int id = Integer.parseInt(lineArray[3]);
                if (id == ids[currentSource]) {
                    line = line + System.lineSeparator();
                    Exporter.exporter(fileWriter, line);
                }
            }

        }

    }

}
