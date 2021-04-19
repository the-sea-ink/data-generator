import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {

   public static Date delayer (Date eventTime) throws IOException, ParseException, java.text.ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int delay = (int) Math.floor(Math.random()*(maxDelay-minDelay+1)+minDelay);
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }
}
