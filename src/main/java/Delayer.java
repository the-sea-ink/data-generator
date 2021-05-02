import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {

    int oooPercentage = ConfigReader.getDelayPercentage();
    int numberOfEvents = (int) (ConfigReader.getRuntime()*1000 / ConfigReader.getTimeBetweenTransactions() ) +1;
    int oooEvents = (int) numberOfEvents * oooPercentage / 100;

    public Delayer() throws IOException, ParseException {
    }

    public static Date delayerRandom(Date eventTime) throws IOException, ParseException, java.text.ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int delay = (int) Math.floor(Math.random()*(maxDelay-minDelay+1)+minDelay);
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

   public Date delayerUserDefined(Date eventTime) throws IOException, ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
       int delay;
       if (distributionCalculation()) {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       else {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;

   }

   public boolean distributionCalculation () {
        int result = (int) Math.random()*numberOfEvents;
        this.numberOfEvents --;
        if (result <= oooEvents) {
            oooEvents --;
            return true;
        }
        else
            return false;
   }
}
