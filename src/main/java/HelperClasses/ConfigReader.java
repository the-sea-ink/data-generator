package HelperClasses;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileReader;
import java.io.IOException;


public class ConfigReader {

    public static Object configReader() throws IOException, ParseException {

        //parse json as an object
        Object obj = new JSONParser().parse(new FileReader("config.json"));
        JSONObject jo = (JSONObject) obj;
        return obj;

    }

    public static String getOutputFile() throws IOException, ParseException {
        //get outputFile value
        JSONObject jo = (JSONObject) configReader();
        return (String) jo.get("outputFile");
    }

    public static int getTransactionsPerSecond () throws IOException, ParseException {
        //get TPS value
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
}
