import HelperClasses.*;
import org.json.simple.parser.ParseException;
import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        //init stream
        Stream dataStream = new Stream();
        //init starting eventTime
        Date eventTime;
        Date processingTime;
        Date startingTime = dataStream.getStartingTime();
        Delayer delayer = new Delayer();
        Videocard machine1 = new Videocard();
        //init file writer
        FileWriter fileWriter = Exporter.exporterInit();

        do {
            //current event string
            if ((TimeHandler.addTimeSeconds(startingTime, ConfigReader.getRuntime())).before(dataStream.getCurrentEventTime()) )
                break;
            List<String> list = new ArrayList<>();

            int eventID = dataStream.getEventID();
            eventTime = dataStream.getCurrentEventTime();
            //processingTime = Delayer.delayerRandom(eventTime);

            processingTime = delayer.delayerUserDefined(eventTime);
            Timestamp evenTimeTimestamp = new Timestamp(eventTime.getTime());
            Timestamp processingTimeTimestamp = new Timestamp(processingTime.getTime());

            //spalte 1
            Converter.listGenerator(list, String.valueOf(eventID));

            //spalte 2
            Converter.listGenerator(list, String.valueOf(eventTime.getTime()));

            //spalte 3
            Converter.listGenerator(list, String.valueOf(processingTime.getTime()));

            //spalte 4
            Converter.listGenerator(list, String.valueOf(machine1.serialNumber));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(machine1.temperature));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(machine1.overheatWarning));

            machine1.Updater();
            dataStream.timeUpdater();

            String string = Converter.stringGenerator(list);

            Exporter.exporter(fileWriter, string);

        }
        while (true);





    }



}


