package HelperClasses;

import HelperClasses.ConfigReader;
import HelperClasses.Event;
import HelperClasses.Logger;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Connector {

    public static void main(String[] args) throws IOException, ParseException {
        sortByProcessingTime();
    }

    public static FileWriter exporterInit() throws IOException, ParseException {
        FileWriter csvWriter = new FileWriter(ConfigReader.getOutputFile());
        return csvWriter;
    }

    public static void exporter(FileWriter csvWriter, String dataString) throws IOException {
        csvWriter.append(dataString);
    }

    public static void closeFile (FileWriter csvWriter) throws IOException {
        csvWriter.flush();
    }

    public static void sortByProcessingTime () throws IOException, ParseException {
        Logger log = new Logger();
        FileWriter csvWriter = new FileWriter("output/sortedByProceesingTimeOutput.csv");
        List<Event> events= new ArrayList<Event>();
        BufferedReader br = new BufferedReader(new FileReader(ConfigReader.getOutputFile()));
        String line;
        while ((line = br.readLine()) != null)
                events.add(Event.parseFromString(line));

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2)
            {
                return event1.processingTime.compareTo(event2.processingTime);
            }
        });
        for (Event event : events){
            csvWriter.write(event.toString());
        }
        csvWriter.flush();
        log.setStreamEnd();
    }

}
