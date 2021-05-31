import HelperClasses.ConfigReader;
import HelperClasses.Splitter;
import HelperClasses.Videocard;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Analyzer {
    //for statistics


    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalEvents();
        int streamDuration = getDurationInMilliseconds();
        int sourcesAmount = ConfigReader.getAmountOfSources();

        Splitter.split();
        System.out.println("-----------------------------------------------------------");
        System.out.println("Stream duration: " + streamDuration + " milliseconds");
        System.out.println("Amount of events: " + eventCount);
        System.out.println("Minimum delay: " + getMinDelay() );
        System.out.println("Maximum delay: " + getMaxDelay());
        for (int currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            String currentInputFile = "output/output" + currentSource +".csv";
            System.out.println("Out of oder percentage, source " + currentSource  +" : "+ outOfOrderPercentage(currentInputFile));
            System.out.println("Critical points: " + getCriticalPointsAmount(currentInputFile) + ", at positions: " + getCriticalPoints(currentInputFile));

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
    public static List<Integer> getCardIDs () throws IOException, ParseException {
        int sources = ConfigReader.getAmountOfSources();
        int sourcesCounter = 0;

        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        int idColumn = 3;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String idString = lineArray[idColumn];
        int id = Integer.parseInt(idString);
        List<Integer> cardIDs  = new ArrayList<>();
        cardIDs.add(id);
        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            idString = lineArray[idColumn];
            id = Integer.parseInt(idString);
            cardIDs.add(id);

        }

        return cardIDs;

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
        int criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;

        int counter = 0;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));
        boolean criticalPoint = Boolean.parseBoolean(lineArray[criticalPointColumn]);
        boolean criticalPointChange = false;
        int criticalPointChanges  = 0;
        int i = 2;

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
            i++;

        }
        return criticalPointChanges;
    }
    public static String getCriticalPoints(String inputFile) throws IOException, ParseException {
        String criticalPoints = "";
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

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
        int i = 1;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            String eventTimeString = lineArray[eventTimeColumn];
            Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
            if (processingTimeDate.getTime() > (eventTimeDate.getTime())) {
                criticalPoint = Boolean.parseBoolean(lineArray[criticalPointColumn]);
                if (criticalPoint != criticalPointChange) {
                    criticalPoints += i + "; ";
                    criticalPointChanges++;
                    criticalPointChange = criticalPoint;
                    //System.out.println(lineArray[0]);
                }
                counter ++;
                i++;
            }


            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

        }

        return criticalPoints;
    }

    }
