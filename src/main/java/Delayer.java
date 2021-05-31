import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
import HelperClasses.Videocard;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {

    //int oooPercentage = ConfigReader.getDelayPercentage();
    //int numberOfEvents = (ConfigReader.getRuntime()*1000 / ConfigReader.getTimeBetweenTransactions() )*ConfigReader.getAmountOfSources() + ConfigReader.getAmountOfSources();
    //int oooEvents = (int) Math.ceil(numberOfEvents * oooPercentage / 100);
    //int ioEvents = numberOfEvents - oooEvents;
    //int currentEvent = 1;

    int minDelay = ConfigReader.getShortestDelayInMilliseconds();
    int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
    int timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
    int delay;

    public Delayer() throws IOException, ParseException {
    }

    public static Date delayerRandom(Date eventTime) throws IOException, ParseException, java.text.ParseException {
       int minDelay = ConfigReader.getShortestDelayInMilliseconds();
       int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
       int delay = (int) Math.floor(Math.random()*(maxDelay-minDelay+1)+minDelay);
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

   public Date delayerRandomDistribution(Date eventTime, Videocard videocard) throws IOException, ParseException {

       //ooo event
       if (distributionCalculation(videocard)) {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       //io event
       else {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;

   }
   public Date delayerConceptDrift(Date eventTime, Videocard videocard) throws IOException, ParseException {

       int conceptDriftStartingEvent = videocard.ioEvents/2;
       //io events
       if (videocard.currentEvent <= conceptDriftStartingEvent || videocard.currentEvent >= conceptDriftStartingEvent + videocard.oooEvents) {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       //ooo events
       else {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       videocard.currentEvent ++;
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

   public boolean distributionCalculation (Videocard videocard) {
        int result = (int) Math.floor((Math.random())*(videocard.numberOfEvents)+1);
        videocard.numberOfEvents --;
        if (result <= videocard.oooEvents) {
            videocard.oooEvents --;
            return true;
        }
        else
            return false;
   }
}
