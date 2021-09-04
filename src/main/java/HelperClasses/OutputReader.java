package HelperClasses;


import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileReader;
import java.io.IOException;

public class OutputReader {

    public static Object outputReader() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(ConfigReader.getOutputFile()));
        JSONObject jo = (JSONObject) obj;
        return obj;
    }


}
