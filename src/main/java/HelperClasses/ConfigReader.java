package HelperClasses;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;
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

}
