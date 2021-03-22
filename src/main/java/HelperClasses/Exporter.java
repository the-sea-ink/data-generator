package HelperClasses;

import java.io.FileWriter;
import java.io.IOException;

public class Exporter {

    public static void exporterInit() throws IOException {
        FileWriter csvWriter = new FileWriter("output/output.csv");
    }

    public static String stringGenerator(int eventID, int sensorID, String eventTime, double waterTemperature){
        //eventID, sensorID, eventTime, waterTemperature
        String dataString = eventID + "," + sensorID + "," + eventTime + "," + waterTemperature + "\n";
        return dataString;
    }

    public static void exporter(FileWriter csvWriter, String dataString) throws IOException {
        csvWriter.append(dataString);
        csvWriter.flush();
        csvWriter.close();
    }

}
