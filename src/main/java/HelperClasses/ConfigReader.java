package HelperClasses;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileReader;
import java.io.IOException;


public class ConfigReader {

    public static Object configReader() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("config.json"));
        //JSONObject jo = (JSONObject) obj;
        return obj;

    }

    public static String getOutputFile() throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        return (String) jo.get("outputFile");
    }

    public static int getTransactionsPerSecond () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long transactionsPerSecondLong = (Long) jo.get("transactionsPerSecond");
        int transactionsPerSecond = transactionsPerSecondLong.intValue();
        return transactionsPerSecond;
    }

    public static int getNumberOfSensors () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long numberOfSensorsLong = (Long) jo.get("numberOfSensors");
        int numberOfSensors = numberOfSensorsLong.intValue();
        return numberOfSensors;
    };

    public static int getRuntime () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long runtimeLong = (Long) jo.get("runtimeInSeconds");
        int runtime = runtimeLong.intValue();
        return runtime;
    };

    public static Date getStartingTime () throws IOException, ParseException, java.text.ParseException {
        JSONObject jo = (JSONObject) configReader();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateStr = (String) jo.get("startingTime");
        Date date = sdf.parse(dateStr);
        return date;
    }

    public static int getTimeBetweenTransactions() throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long timeBetweenTransactionsLong = (Long) jo.get("milliSecondsBetweenTransactions");
        int timeBetweenTransactions = timeBetweenTransactionsLong.intValue();
        return timeBetweenTransactions;

    }

    public static int getEventTimeColumn() throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long eventTimeColumnLong = (Long) jo.get("eventTimeColumn");
        int eventTimeColumn = eventTimeColumnLong.intValue();
        return eventTimeColumn;
    }

    public static int getProcessingTimeColumn () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long processingTimeColumnLong = (long) jo.get("processingTimeColumn");
        int processingTimeColumn = processingTimeColumnLong.intValue();
        return processingTimeColumn;
    }

    public static int getShortestDelayInMilliseconds () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long shortestDelayInMillisecondsLong = (Long) jo.get("shortestDelayInMilliseconds");
        int shortestDelayInMilliseconds = shortestDelayInMillisecondsLong.intValue();
        return shortestDelayInMilliseconds;
    }

    public static int getLongestDelayInMilliseconds () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long longestDelayInMillisecondsLong = (Long) jo.get("longestDelayInMilliseconds");
        int longestDelayInMilliseconds = longestDelayInMillisecondsLong.intValue();
        return longestDelayInMilliseconds;
    }

    public static int getDelayPercentage () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long delayPercentageLong = (Long) jo.get("delayPercentage");
        int delayPercentage = delayPercentageLong.intValue();
        return delayPercentage;
    }

    public static int getCriticalPointColumn () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long criticalPointolumnLong = (Long) jo.get("criticalPointColumn");
        int criticalPointolumn = criticalPointolumnLong.intValue();
        return criticalPointolumn;

    }

    public static int getAmountOfSources () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long amountOfSourcesLong = (Long) jo.get("amountOfSources");
        int amountOfSources = amountOfSourcesLong.intValue();
        return amountOfSources;
    }

    public static int getDelayPattern () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long delayPatternLong = (Long) jo.get("delayPattern");
        int delayPattern = delayPatternLong.intValue();
        return delayPattern;
    }

    public static boolean getExceptionSource () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        //Long standOutSourceLong = (Long) jo.get("exceptionSource");
        boolean standoutSource = (boolean) jo.get("exceptionSource");
        return standoutSource;
    }

    public static int getExceptionSourceNumber () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long standOutSourceNumberLong = (Long) jo.get("exceptionSourceNumber");
        int standOutSourceNumber = standOutSourceNumberLong.intValue();
        return standOutSourceNumber;
    }

    public static int getExceptionSourcePattern () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long exceptionSourcePatternLong =  (Long) jo.get("exceptionSourcePattern");
        int exceptionSourcePattern = exceptionSourcePatternLong.intValue();
        return exceptionSourcePattern;
    }

    public static int getExceptionSourceDelay () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long exceptionSourceDelayLong = (Long) jo.get("exceptionSourceDelay");
        int exceptionSourceDelay = exceptionSourceDelayLong.intValue();
        return exceptionSourceDelay;
    }

    public static int getOutlierOoo (int sourceID) throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        JSONArray arr = (JSONArray) jo.get("outliers");
        for (int i = 0; i < arr.size(); i++ ) {
            JSONObject line = (JSONObject) arr.get(i);
            Long outlierLong = Long.valueOf( (Long) line.get("sourceID"));
            int outlier = outlierLong.intValue();
            if(outlier == sourceID) {
                Long oooPercLong = Long.valueOf((Long)line.get("oooPercentage"));
                return oooPercLong.intValue();
            }
        }
        return -1;
    }

    public static int getOutlierPattern (int sourceID) throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        JSONArray arr = (JSONArray) jo.get("outliers");
        for (int i = 0; i < arr.size(); i++ ) {
            JSONObject line = (JSONObject) arr.get(i);
            Long outlierLong = Long.valueOf((Long) line.get("sourceID"));
            int outlier = outlierLong.intValue();
            if(outlier == sourceID) {
                Long pattern = Long.valueOf((Long)line.get("pattern"));
                return pattern.intValue();
            }
        }
        return -1;
    }

    public static int getConnectionLossDuration () throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long connectionLossDurationInSecondsLong = (Long) jo.get("connectionLossDurationInSeconds");
        int connectionLossDurationInSeconds = connectionLossDurationInSecondsLong.intValue();
        return connectionLossDurationInSeconds;
    }

}
