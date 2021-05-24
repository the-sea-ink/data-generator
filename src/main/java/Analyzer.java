import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class Analyzer {
    //for statistics


    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalEvents();
        int streamDuration = getDurationInMilliseconds();

        System.out.println("-----------------------------------------------------------");
        System.out.println("Stream duration: " + streamDuration + " milliseconds");
        System.out.println("Amount of events: " + eventCount);
        System.out.println("Minimum delay: " + getMinDelay() );
        System.out.println("Maximum delay: " + getMaxDelay());
        System.out.println("Out of oder percentage: " + outOfOrderPercentage());
        System.out.println("Critical points: " + getCriticalPoints());
        System.out.println("-----------------------------------------------------------");

    }
    public static int getTotalEvents() throws IOException {
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

        String startingTime = firstLineArray[eventTimeColumn];

        Date dateStart = new Date(Long.parseLong(startingTime));

        //get last line
        String lastLine = "";
        while((br.readLine()) != null)
            lastLine = br.readLine();
        String[] lastLineArray = lastLine.split(",");
        String endTime = lastLineArray[eventTimeColumn];
        Date dateEnd = new Date(Long.parseLong(endTime));
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


        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = new Date(Long.parseLong(eventTimeString));

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        Date delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
        int minDelay = (int) (delayDate.getTime());

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));


            delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();


            if (diff < minDelay)
                minDelay = (int) (delayDate.getTime());

        }
        return minDelay;

    }
    public static int getMaxDelay () throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = new Date(Long.parseLong(eventTimeString));

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        Date delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
        int maxDelay = (int) (delayDate.getTime());

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));


            delayDate = new Date(processingTimeDate.getTime() - evenTimeDate.getTime());
            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();


            if (diff > maxDelay)
                maxDelay = (int) (delayDate.getTime());

        }
        return maxDelay;

    }
    public static double outOfOrderPercentage () throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
        int criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;

        int counter = 0;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            String eventTimeString = lineArray[eventTimeColumn];
            Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
            if (processingTimeDate.getTime() > (eventTimeDate.getTime())) {
                counter ++;
            }

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

        }
        return counter/(double)getTotalEvents();

    }
    public static int getCriticalPoints() throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
        int criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;

        int counter = 0;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));
        boolean criticalPoint = Boolean.parseBoolean(lineArray[criticalPointColumn]);
        boolean criticalPointChange = false;
        int criticalPointChanges  = 0;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            String eventTimeString = lineArray[eventTimeColumn];
            Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
            if (processingTimeDate.getTime() > (eventTimeDate.getTime())) {
                criticalPoint = Boolean.parseBoolean(lineArray[criticalPointColumn]);
                if (criticalPoint != criticalPointChange) {
                    criticalPointChanges++;
                    criticalPointChange = criticalPoint;
                    //System.out.println(lineArray[0]);
                }
                counter ++;
            }


            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

        }
        return criticalPointChanges;
    }
}
