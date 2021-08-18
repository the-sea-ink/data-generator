import HelperClasses.ConfigReader;
import HelperClasses.ReverseLineInputStream;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

public class Analyzer {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalEvents("output/output.csv");
        int streamDuration = getDurationInMilliseconds();
        int sourcesAmount = ConfigReader.getAmountOfSources();
        FileWriter fileWriter = new FileWriter("output/analyzerOutput.csv");
        String line = "";

        Splitter.split();
        System.out.println("-----------------------------------------------------------");

        line = "Stream duration: " + streamDuration/1000 + " seconds = " + streamDuration + " milliseconds";
        System.out.println(line);
        Connector.exporter(fileWriter, line + System.lineSeparator());

        line ="Total amount of events: " + eventCount;
        System.out.println(line);
        Connector.exporter(fileWriter, line+ System.lineSeparator());
        System.out.println();

        for (int currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            String currentInputFile = "output/output" + currentSource +".csv";

            line = "Source " + (currentSource +1) + ": ";
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());
            line = "Amount of events: " + getTotalEvents(currentInputFile);
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());
            line = "Minimum delay: " + getMinDelay() ;
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());
            line ="Maximum delay: " + getMaxDelay(currentInputFile);
            System.out.println(line);
            Connector.exporter(fileWriter, line+System.lineSeparator());

            if (ConfigReader.getOutlierPattern(currentSource+1) == 3 || ConfigReader.getDelayPattern() == 3 ){
                line = "Out of order percentage: " + outOfOrderPercentage(currentInputFile) +
                        ", connection loss duration: " + getMaxDelay(currentInputFile);
            }

            else{
                line = "Out of order percentage: " + outOfOrderPercentage(currentInputFile);
            }
            System.out.println(line);
            Connector.exporter(fileWriter, line);

            line = "Critical points: " + getCriticalPointsAmount(currentInputFile) + ", at positions: " + getCriticalPoints(currentInputFile) + System.lineSeparator();
            System.out.println("Critical points: " + getCriticalPointsAmount(currentInputFile));
            Connector.exporter(fileWriter, line);
            System.out.println();
        }
        //System.out.println("Critical points: " + getCriticalPointsAmount() + ", at positions: " + getCriticalPoints());
        System.out.println("-----------------------------------------------------------");

    }
    public static int getTotalEvents(String inputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        br.readLine();
        int count = 0;
        while((br.readLine()) != null)
            count++;
        return count;

    }
    public static int getDurationInMilliseconds() throws IOException, ParseException, java.text.ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        //skip header row
        br.readLine();
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
        //skip header row
        br.readLine();
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


            if (diff > maxDelay) {
                maxDelay = (int) (delayDate.getTime());
            }



        }
        return maxDelay;

    }
    public static double outOfOrderPercentage (String inputFile) throws IOException, ParseException {
        int counter = 0;

        BufferedReader br = new BufferedReader (new InputStreamReader (new ReverseLineInputStream(new File(inputFile))));

        String line = br.readLine();
        Event lastEvent = Event.parseFromString(line);
        Date minProcessingTime = lastEvent.processingTime;

        while ((line = br.readLine()) != null) {
            Event currentEvent = Event.parseFromString(line);
            if (minProcessingTime.before(currentEvent.processingTime))
                ///ooo event
                counter++;
            else
                minProcessingTime = currentEvent.processingTime;
            }

        return counter/(double)getTotalEvents("output/output.csv")*ConfigReader.getAmountOfSources();

    }
    public static int getCriticalPointsAmount(String inputFile) throws IOException, ParseException {
        BufferedReader br = new BufferedReader (new InputStreamReader (new ReverseLineInputStream(new File(inputFile))));
        int criticalPointsAmount  = 0;
        int i;
        String line = br.readLine();

        Event lastEvent = Event.parseFromString(line);
        Date minProcessingTime = lastEvent.processingTime;
        boolean sensorWarning = lastEvent.sensorWarning;

        while ((line = br.readLine()) != null) {
            Event currentEvent = Event.parseFromString(line);
            if (minProcessingTime.before(currentEvent.processingTime) && currentEvent.sensorWarning!=sensorWarning) {
                ///ooo event & critical point
                criticalPointsAmount++;
            }

            else{
                minProcessingTime = currentEvent.processingTime;
            }
            sensorWarning = currentEvent.sensorWarning;

        }

        return criticalPointsAmount;
    }
    public static String getCriticalPoints(String inputFile) throws IOException, ParseException {
        BufferedReader br = new BufferedReader (new InputStreamReader (new ReverseLineInputStream(new File(inputFile))));
        String criticalPoints = "";
        int i;
        String line = br.readLine();


        Event lastEvent = Event.parseFromString(line);
        Date minProcessingTime = lastEvent.processingTime;
        boolean sensorWarning = lastEvent.sensorWarning;

        while ((line = br.readLine()) != null) {
            String[] lineArray = line.split(",");
            Event currentEvent = Event.parseFromString(line);
            if (minProcessingTime.before(currentEvent.processingTime) && currentEvent.sensorWarning!=sensorWarning) {
                ///ooo event & critical point
                i = Integer.parseInt(lineArray[0]);
                criticalPoints += i + "; ";
            }

            else{
                minProcessingTime = currentEvent.processingTime;
            }
            sensorWarning = currentEvent.sensorWarning;

        }

        return criticalPoints;
    }

    }
