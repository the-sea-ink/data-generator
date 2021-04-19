import HelperClasses.ConfigReader;
import HelperClasses.Converter;
import HelperClasses.Exporter;
import HelperClasses.TimeHandler;
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
        //init file writer
        FileWriter fileWriter = Exporter.exporterInit();

        //Timestamp timest = new Timestamp (TimeHandler.addTimeSeconds(startingTime, ConfigReader.getRuntime()).getTime());
        //System.out.println(timest);


        do {
            //current event string
            if ((TimeHandler.addTimeSeconds(startingTime, ConfigReader.getRuntime())).before(dataStream.getCurrentEventTime()) )
                break;
            List<String> list = new ArrayList<>();

            int eventID = dataStream.getEventID();
            eventTime = dataStream.getCurrentEventTime();
            processingTime = Delayer.delayer(eventTime);
            Timestamp evenTimeTimestamp = new Timestamp(eventTime.getTime());
            Timestamp processingTimeTimestamp = new Timestamp(processingTime.getTime());

            Converter.listGenerator(list, String.valueOf(eventID));
            Converter.listGenerator(list, String.valueOf(evenTimeTimestamp));
            Converter.listGenerator(list, String.valueOf(processingTimeTimestamp));

            dataStream.timeUpdater();

            String string = Converter.stringGenerator(list);

            Exporter.exporter(fileWriter, string);

        }
        while (true);





    }



}


