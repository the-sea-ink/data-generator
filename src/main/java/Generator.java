import HelperClasses.*;
import org.json.simple.parser.ParseException;
import sun.net.ExtendedOptionsHelper;

import java.util.Date;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class Generator {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        Logger log = new Logger();
        //init stream
        Stream dataStream = new Stream();
        //init starting eventTime
        Date eventTime;
        Date processingTime;
        Date startingTime = dataStream.getStartingTime();
        Delayer delayer = new Delayer();
        int amountOfSources = ConfigReader.getAmountOfSources();
        List<Videocard> videoCards  = new ArrayList<>();
        for (int i = 0; i <= amountOfSources; i++) {
            videoCards.add(new Videocard(i));
        }

        int cardUpdater = 0;

        //init file writer
        FileWriter fileWriter = Exporter.exporterInit();

        do {
            //current event string
            if ((TimeHandler.addTimeSeconds(startingTime, ConfigReader.getRuntime())).before(dataStream.getCurrentEventTime()) )
                break;

            List<String> list = new ArrayList<>();

            int eventID = dataStream.getEventID();
            eventTime = dataStream.getCurrentEventTime();

            processingTime = delayer.delayerRandomDistribution(eventTime, videoCards.get(cardUpdater));

            //spalte 1
            Converter.listGenerator(list, String.valueOf(eventID));

            //spalte 2
            Converter.listGenerator(list, String.valueOf(eventTime.getTime()));

            //spalte 3
            Converter.listGenerator(list, String.valueOf(processingTime.getTime()));

            //spalte 4
            Converter.listGenerator(list, String.valueOf(videoCards.get(cardUpdater).serialNumber));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(videoCards.get(cardUpdater).temperature));

            //spalte 5
            Converter.listGenerator(list, String.valueOf(videoCards.get(cardUpdater).overheatWarning));

            videoCards.get(cardUpdater).Updater();


            cardUpdater++;


            if (cardUpdater >= amountOfSources) {
                dataStream.timeUpdater();
                cardUpdater = 0;
            }

            String string = Converter.stringGenerator(list);
            Exporter.exporter(fileWriter, string);

        }
        while (true);
        log.setStreamEnd();
    }



}


