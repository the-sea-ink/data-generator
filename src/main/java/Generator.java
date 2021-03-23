
import HelperClasses.ConfigReader;
import HelperClasses.Converter;
import HelperClasses.Exporter;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Generator {

    public static void main(String[] args) throws IOException, ParseException {

        //init
        SensorDataGenerator sensor = new SensorDataGenerator(ConfigReader.getNumberOfSensors());
        FileWriter fileWriter = Exporter.exporterInit();

        for (long stop = System.nanoTime()+ TimeUnit.SECONDS.toNanos(ConfigReader.getRuntime()); stop>System.nanoTime();) {
            List<String> list = new ArrayList<>();

            int eventID = sensor.getEventID();
            int sensorID = sensor.getSensorID();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long eventTime = timestamp.getTime();

            Converter.listGenerator(list, String.valueOf(eventID));
            Converter.listGenerator(list, String.valueOf(sensorID));
            Converter.listGenerator(list, String.valueOf(eventTime));

            String string = Converter.stringGenerator(list);


            Exporter.exporter(fileWriter, string);

        }





    }



}


