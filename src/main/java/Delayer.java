import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {

    int oooPercentage = ConfigReader.getDelayPercentage();
    int numberOfEvents = (ConfigReader.getRuntime()*1000 / ConfigReader.getTimeBetweenTransactions() ) +1;
    int oooEvents = (int) Math.ceil(numberOfEvents * oooPercentage / 100);
    int ioEvents = numberOfEvents - oooEvents;
    int currentEvent = 1;

    public Delayer() throws IOException, ParseException {
    }

    public static Date delayerRandom(Date eventTime) throws IOException, ParseException, java.text.ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int delay = (int) Math.floor(Math.random()*(maxDelay-minDelay+1)+minDelay);
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

   public Date delayerRandomDistribution(Date eventTime) throws IOException, ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
       int delay;
       //ooo event
       if (distributionCalculation()) {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       //io event
       else {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;

   }
   public Date delayerConceptDrift(Date eventTime) throws IOException, ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
       int delay;
       int conceptDriftStartingEvent = this.ioEvents/2;
       //io events
       if (this.currentEvent <= conceptDriftStartingEvent || this.currentEvent >= conceptDriftStartingEvent + this.oooEvents) {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       //ooo events
       else {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       currentEvent ++;
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

   public boolean distributionCalculation () {
        int result = (int) Math.floor((Math.random())*(numberOfEvents)+1);
        this.numberOfEvents --;
        if (result <= this.oooEvents) {
            this.oooEvents --;
            return true;
        }
        else
            return false;
   }
}
