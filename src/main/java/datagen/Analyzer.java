package datagen;

import HelperClasses.*;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

public class Analyzer {

    private class AnalzyerResults {
        long totalEvents;
        long maxDelay;
        long minDelay;
        double oooPercentage;
        long critPointsTotalAmount;
        String criticalPoints;
        Date streamEnd;
    }

    private class TotalEventCounter {
        int totalEvents = 0;

        public void processEvent(Event event) {
            totalEvents += 1;
        }

    }

    private class StreamDurationCalculator {
        Date streamStart = new Date();
        Date streamEnd = new Date();
        long streamDuration = 0;

        public void processEvent(Event event) {
            if (streamStart == null || streamEnd == null) {
                streamStart = event.eventTime;
                streamEnd = event.eventTime;
            }
            streamStart = event.eventTime;
            streamDuration = streamEnd.getTime() - streamStart.getTime();

        }
    }

    private class ExtremumFinder {
        long maxDelay = 0;
        long minDelay = Long.MAX_VALUE;

        public void processEvent(Event event) {
            long currentDelay = event.getDelay();
            minDelay = Long.min(minDelay, currentDelay);
            maxDelay = Long.max(maxDelay, currentDelay);
        }
    }

    private class OooCalculator {
        private long totalAmount = 0;
        private Date minProcessingTime;

        long oooFound = 0;

        public void processEvent(Event event) {
            totalAmount += 1;
            if (minProcessingTime == null) {
                minProcessingTime = event.processingTime;
                return;
            }
            if (minProcessingTime.before(event.processingTime))
                oooFound++;
            else
                minProcessingTime = event.processingTime;
        }

        public double getOooPercentage() {
            return oooFound / (double) totalAmount * 100;
        }
    }

    private class CritPointsFinder {
        long totalAmount = 0;
        private Date minProcessingTime;
        private boolean minSensorWarning;
        private StringBuilder critPointsString = new StringBuilder();

        public void processEvent(Event event) {
            if (minProcessingTime == null) {
                minProcessingTime = event.processingTime;
                minSensorWarning = event.sensorWarning;
                return;
            }
            if (minProcessingTime.before(event.processingTime) && event.sensorWarning != minSensorWarning) {
                totalAmount++;
                critPointsString.append(event.id).append(", ");
            } else {
                minSensorWarning = event.sensorWarning;
            }
            minProcessingTime = event.processingTime;


        }

        public String getCriticalPoints() {
            return critPointsString.toString();
        }
    }

    public static void main(String[] args) throws ParseException, java.text.ParseException, IOException {
        int totalEvents = 0;
        int sourcesAmount = ConfigReader.getAmountOfSources();
        FileWriter fileWriter = new FileWriter("output/analyzerOutput.csv", true);
        String line = "";

        Splitter.split();
        System.out.println("-----------------------------------------------------------");


        for (int currentSource = 0; currentSource < sourcesAmount; currentSource++) {
            String currentInputFile = "output/output" + currentSource + ".csv";
            AnalzyerResults results = new Analyzer().analyzeReverse(currentInputFile);

            line = "Source " + (currentSource + 1) + ": ";
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());

            long currentEventAmount = results.totalEvents;
            line = "Amount of events: " + currentEventAmount;
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());

            line = "Minimum delay: " + results.minDelay;
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());

            line = "Maximum delay: " + results.maxDelay;
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());

            if (ConfigReader.getOutlierPattern(currentSource + 1) == 3 || ConfigReader.getDelayPattern() == 3) {
                line = "Out of order percentage: " + results.oooPercentage +
                        ", longest connection loss duration: " + results.maxDelay + " seconds";
            } else {
                line = "Out of order percentage: " + results.oooPercentage;
            }
            System.out.println(line);
            Connector.exporter(fileWriter, line + System.lineSeparator());

            line = "Critical points: " + results.critPointsTotalAmount + ", at positions: " + results.criticalPoints + System.lineSeparator();
            System.out.println("Critical points: " + results.critPointsTotalAmount);
            Connector.exporter(fileWriter, line+System.lineSeparator());
            System.out.println();

            totalEvents += results.totalEvents;
        }
        long streamDuration = ConfigReader.getStreamDuration();
        line = "Stream duration: " + streamDuration + " seconds";
        System.out.println(line);
        Connector.exporter(fileWriter, line + System.lineSeparator());
        line = "Total events: " + totalEvents;
        System.out.println(line);
        Connector.exporter(fileWriter, line + System.lineSeparator());
        Connector.exporter(fileWriter, "___________________________________________________" + System.lineSeparator());
        System.out.println("-----------------------------------------------------------");
        fileWriter.flush();
        fileWriter.close();
    }

    public AnalzyerResults analyzeReverse(String inputFile) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(new File(inputFile))));
        String line;
        TotalEventCounter teCounter = new TotalEventCounter();
        ExtremumFinder exFinder = new ExtremumFinder();
        OooCalculator oooCalculator = new OooCalculator();
        CritPointsFinder cpFinder = new CritPointsFinder();
        StreamDurationCalculator streamDurationCalculator = new StreamDurationCalculator();
        while ((line = br.readLine()) != null) {
            Event event = Event.parseFromString(line);
            teCounter.processEvent(event);
            exFinder.processEvent(event);
            oooCalculator.processEvent(event);
            cpFinder.processEvent(event);
            streamDurationCalculator.processEvent(event);

        }
        AnalzyerResults results = new AnalzyerResults();
        results.totalEvents = teCounter.totalEvents;
        results.maxDelay = exFinder.maxDelay;
        results.minDelay = exFinder.minDelay;
        results.oooPercentage = oooCalculator.getOooPercentage();
        results.criticalPoints = cpFinder.getCriticalPoints();
        results.critPointsTotalAmount = cpFinder.totalAmount;
        results.streamEnd = streamDurationCalculator.streamEnd;
        return results;
    }

    public static int getTotalEvents(String inputFile, boolean skip) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        if (skip)
            br.readLine();
        int count = 0;
        while ((br.readLine()) != null)

            count++;
        return count;

    }

}