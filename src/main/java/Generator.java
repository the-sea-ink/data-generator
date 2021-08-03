import HelperClasses.*;
import org.json.simple.parser.ParseException;

import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        /*
        {"sourceID":1, "pattern":1, "oooPercentage" : 50},
        {"sourceID":2, "pattern":2, "oooPercentage" : 50},
        {"sourceID":3, "pattern":3, "connectionLossDurationInSeconds" : 5 }
         */

        Logger log = new Logger();
        //init stream
        Stream dataStream = new Stream();
        //init starting eventTime
        Date eventTime;
        Date processingTime;
        Date startingTime = dataStream.getStartingTime();

        int amountOfSources = ConfigReader.getAmountOfSources();
        List<InsulinSensor> insulinSensors  = new ArrayList<>();
        for (int i = 0; i <= amountOfSources; i++) {
            insulinSensors.add(new InsulinSensor(i));
        }

        int sensorID = 0;

        //init file writer
        FileWriter fileWriter = Connector.exporterInit();

        do {
            Delayer delayer = new Delayer();
            //current event string
            if ((TimeHandler.addTimeSeconds(startingTime, ConfigReader.getRuntime())).before(dataStream.getCurrentEventTime()) )
                break;

            List<String> list = new ArrayList<>();

            int eventID = dataStream.getEventID();
            eventTime = dataStream.getCurrentEventTime();

            processingTime = delayer.delayer(insulinSensors.get(sensorID).pattern,eventTime, insulinSensors.get(sensorID), sensorID);


            //spalte 1
            Converter.listGenerator(list, String.valueOf(eventID));

            //spalte 2
            Converter.listGenerator(list, String.valueOf(eventTime.getTime()));

            //spalte 3
            Converter.listGenerator(list, String.valueOf(processingTime.getTime()));

            //spalte 4
            Converter.listGenerator(list, String.valueOf(insulinSensors.get(sensorID).serialNumber));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(insulinSensors.get(sensorID).glucoseAmount));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(insulinSensors.get(sensorID).highGluckoseWarning));

            insulinSensors.get(sensorID).Updater();


            sensorID++;


            if (sensorID >= amountOfSources) {
                dataStream.timeUpdater();
                sensorID = 0;
            }

            String string = Converter.stringGenerator(list);
            Connector.exporter(fileWriter, string);

        }
        while (true);

        log.setStreamEnd();
    }



}


