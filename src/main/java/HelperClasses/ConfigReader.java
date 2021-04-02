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

    public static Integer getTimeBetweenTransactions() throws IOException, ParseException {
        JSONObject jo = (JSONObject) configReader();
        Long timeBetweenTransactionsLong = (Long) jo.get("milliSecondsBetweenTransactions");
        int timeBetweenTransactions = timeBetweenTransactionsLong.intValue();
        return timeBetweenTransactions;

    }

}
