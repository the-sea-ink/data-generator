import HelperClasses.ConfigReader;
import HelperClasses.Converter;
import HelperClasses.Exporter;
import org.json.simple.parser.ParseException;
import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Generator {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        //init stream
        Stream dataStream = new Stream();
        //init starting date
        Date date = dataStream.getCurrentEventTime();
        //init file writer
        FileWriter fileWriter = Exporter.exporterInit();


        for (long stop = System.nanoTime()+ TimeUnit.SECONDS.toNanos(ConfigReader.getRuntime()); stop>System.nanoTime();) {
            //current event string
            List<String> list = new ArrayList<>();

            int eventID = dataStream.getEventID();
            date = dataStream.getCurrentEventTime();
            Timestamp ts = new Timestamp(date.getTime());

            Converter.listGenerator(list, String.valueOf(eventID));
            Converter.listGenerator(list, String.valueOf(ts));

            dataStream.timeUpdater();

            String string = Converter.stringGenerator(list);

            Exporter.exporter(fileWriter, string);

        }





    }



}


