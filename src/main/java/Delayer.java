import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
import HelperClasses.Videocard;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {

    int minDelay = ConfigReader.getShortestDelayInMilliseconds();
    int maxDelay = ConfigReader.getLongestDelayInMilliseconds();
    int timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
    int delay;
    Date connectionLossDate;
    Date connectionRecoveryDate;
    boolean firstConnectionLossCheck = false;


    public Delayer() throws IOException, ParseException {
    }

    public  Date delayer(int i, Date eventTime, Videocard videocard) throws IOException, ParseException {
        if (i == 1)
            return delayerRandomDistribution(eventTime, videocard);
        else if (i == 2)
            return delayerConceptDrift(eventTime, videocard);
        else
            return delayerConnectionLoss(eventTime,videocard);
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
        int result = (int) Math.floor((Math.random())*(videocard.numberOfEvents-1)+1);
        videocard.numberOfEvents --;
        if (result > videocard.oooEvents) {
           return false;
        }
        else {
            videocard.oooEvents --;
            return true;
        }
   }

   public Date delayerConnectionLoss (Date eventTime, Videocard videocard) throws IOException, ParseException {
       int conceptDriftStartingEvent = videocard.ioEvents/2;
       int delayConnectionLoss = ConfigReader.getConnectionLossDuration();
       Date processingTime;
       //io events
       if (videocard.currentEvent <= conceptDriftStartingEvent || videocard.currentEvent >= conceptDriftStartingEvent + videocard.oooEvents) {
           delay =  (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
           processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);

       }
       //ooo events
       else if (!firstConnectionLossCheck) {
           firstConnectionLossCheck = true;
           this.connectionLossDate = eventTime;
           this.connectionRecoveryDate = TimeHandler.addTimeMilliseconds(eventTime, delayConnectionLoss*1000);
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
           processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);

       }else {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
           if (eventTime.getTime() < this.connectionRecoveryDate.getTime())
               processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);
           else
                processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);

       }
       videocard.currentEvent ++;

       return processingTime;
   }
}
