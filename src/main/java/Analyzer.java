import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Analyzer {
    //for statistics

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalTransactions();
        int streamDuration = getDurationInMilliseconds();

        System.out.println("-----------------------------------------------------------");
        System.out.println("Stream duration: " + streamDuration + " milliseconds");
        System.out.println("Amount of events: " + eventCount);
        System.out.println("Minimum delay: ");
        System.out.println("Maximum delay: ");
        System.out.println("Delayed percentage: ");
        System.out.println("-----------------------------------------------------------");

    }
    public static int getTotalTransactions() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        int count = 0;
        while((br.readLine()) != null)
            count++;
        return count;

    }
    public static int getDurationInMilliseconds() throws IOException, ParseException, java.text.ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        //get first line
        String firstLine = br.readLine();
        String[] firstLineArray = firstLine.split(",");
        int eventTimeColumn = ConfigReader.getEventTimeColumn()-1;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String startingTime = firstLineArray[eventTimeColumn];
        Date dateStart = sdf.parse(startingTime);
        Timestamp tsStart = new Timestamp(dateStart.getTime());

        //get last line
        String lastLine = "";
        while((br.readLine()) != null)
            lastLine = br.readLine();
        String[] lastLineArray = lastLine.split(",");
        String endTime = lastLineArray[eventTimeColumn];
        Date dateEnd = sdf.parse(endTime);
        Timestamp tsEnd = new Timestamp(dateEnd.getTime());

        Date streamDurationDate = new Date (dateEnd.getTime() - dateStart.getTime());
        int streamDurationMilliseconds = (int) (streamDurationDate.getTime());

        return streamDurationMilliseconds;
    }
    public static int getMinDelay () throws IOException, ParseException, java.text.ParseException {
        //read output
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = sdf.parse(eventTimeString);

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = sdf.parse(processingTimeString);

        Date delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
        int minDelay = (int) (delayDate.getTime());

        while ((br.readLine()) != null) {

            line = br.readLine();
            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = sdf.parse(eventTimeString);

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = sdf.parse(processingTimeString);


            delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();


            if (diff < minDelay)
                minDelay = (int) (delayDate.getTime());

        }
        return minDelay;

    }
    public static void getMaxDelay () {}

}
