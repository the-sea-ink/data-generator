import HelperClasses.ConfigReader;
import HelperClasses.Exporter;
import HelperClasses.Splitter;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

public class Analyzer {
    //for statistics


    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalEvents();
        int streamDuration = getDurationInMilliseconds();
        int sourcesAmount = ConfigReader.getAmountOfSources();
        FileWriter fileWriter = new FileWriter("output/analyzerOutput.csv");
        String line = "";

        Splitter.split();
        System.out.println("-----------------------------------------------------------");

        System.out.println("Stream duration: " + streamDuration + " milliseconds");
        line = "Stream duration:" + streamDuration +" milliseconds" + System.lineSeparator();
        Exporter.exporter(fileWriter, line);

        System.out.println("Amount of events: " + eventCount);
        line ="Amount of events: " + eventCount + System.lineSeparator();
        Exporter.exporter(fileWriter, line);
        System.out.println();


        for (int currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            String currentInputFile = "output/output" + currentSource +".csv";

            System.out.println("Source " + (currentSource +1) + ": ");

            System.out.println("Minimum delay: " + getMinDelay() );
            line = "Minimum delay: " + getMinDelay() + System.lineSeparator();
            Exporter.exporter(fileWriter, line);

            System.out.println("Maximum delay: " + getMaxDelay(currentInputFile));
            line = "Maximum delay: " + getMaxDelay(currentInputFile) + System.lineSeparator();
            Exporter.exporter(fileWriter, line);

            if (ConfigReader.getOutlierPattern(currentSource+1) == 3 || ConfigReader.getDelayPattern() == 3 ){
                line = "Out of oder percentage: " + outOfOrderPercentage(currentInputFile) +
                        ", connection loss duration: " + getMaxDelay(currentInputFile);
            }

            else{
                line = "Out of oder percentage: " + outOfOrderPercentage(currentInputFile);
            }
            System.out.println(line);

            Exporter.exporter(fileWriter, line);

            System.out.println("Critical points: " + getCriticalPointsAmount(currentInputFile));
            line = "Critical points: " + getCriticalPointsAmount(currentInputFile) + ", at positions: " + getCriticalPoints(currentInputFile) + System.lineSeparator();
            Exporter.exporter(fileWriter, line);
            System.out.println();
        }
        //System.out.println("Critical points: " + getCriticalPointsAmount() + ", at positions: " + getCriticalPoints());
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
        String nullLineCheck = "";

        while ((nullLineCheck = br.readLine()) != null) {
            lastLine = nullLineCheck;
        }

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
    public static int getMaxDelay (String inputFile) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

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
    public static double outOfOrderPercentage (String inputFile) throws IOException, ParseException {
        int counter = 0;

        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
        int criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;

        BufferedReader br = new BufferedReader(new FileReader(inputFile));

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

        return counter/(double)getTotalEvents()*ConfigReader.getAmountOfSources();

    }
    public static int getCriticalPointsAmount(String inputFile) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
        int overheatColumn = ConfigReader.getCriticalPointColumn() -1;


        String line = br.readLine();
        String[] lineArray = line.split(",");

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));
        boolean overheatWarningCurr;
        boolean overheatWarningPrev = false;
        int criticalPointsAmount  = 0;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            String eventTimeString = lineArray[eventTimeColumn];
            Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
            overheatWarningCurr = Boolean.parseBoolean(lineArray[overheatColumn]);
            if (processingTimeDate.getTime() > (eventTimeDate.getTime()) && overheatWarningCurr != overheatWarningPrev) {
                    criticalPointsAmount++;
                    overheatWarningPrev = overheatWarningCurr;
                    //System.out.println(lineArray[0]);

            }
            else
                overheatWarningPrev = Boolean.parseBoolean(lineArray[overheatColumn]);

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

        }
        return criticalPointsAmount;
    }
    public static String getCriticalPoints(String inputFile) throws IOException, ParseException {
        String criticalPoints = "";
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
        int overheatColumn = ConfigReader.getCriticalPointColumn() -1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));
        boolean overheatWarningCurr;
        boolean overheatWarningPrev = false;
        int i;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            String eventTimeString = lineArray[eventTimeColumn];
            Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
            overheatWarningCurr = Boolean.parseBoolean(lineArray[overheatColumn]);
            if (processingTimeDate.getTime() > (eventTimeDate.getTime()) && overheatWarningCurr != overheatWarningPrev) {
                i = Integer.parseInt(lineArray[0]);
                criticalPoints += i + "; ";
                overheatWarningPrev = overheatWarningCurr;
                    //System.out.println(lineArray[0]);
            }
            else
                overheatWarningPrev = Boolean.parseBoolean(lineArray[overheatColumn]);

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

        }

        return criticalPoints;
    }

    }
