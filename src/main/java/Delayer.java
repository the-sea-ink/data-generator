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
    int conceptDriftState = 0;
    int gaussNumber = -1;
    int gradualDriftEventsAmountStateOne = -1;
    int gradualDriftEventsAmountStateTwo = -1;
    int currentGradualDriftEventOoo = -1;
    int currentGradualDriftEventIo = -1;
    int gradualDriftHelper = 1;
    boolean init = false;
    
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
        //check state
       if (conceptDriftState == 1 && !this.init) {
           stateOneCalculation(videocard);
           this.init = true;
       }
       //check if wechsel im state 1
       if (this.currentGradualDriftEventOoo == 0 && this.currentGradualDriftEventIo == 0 && this.gradualDriftEventsAmountStateOne > 0){
            this.gradualDriftEventsAmountStateOne--;
            this.currentGradualDriftEventOoo = this.gradualDriftEventsAmountStateOne;
            this.currentGradualDriftEventIo = this.gradualDriftEventsAmountStateOne;
       }
       //check if wechsel im state 2
        if (this.conceptDriftState == 2 && !this.init) {
            if (this.currentGradualDriftEventOoo == this.gradualDriftHelper && this.currentGradualDriftEventIo == this.gradualDriftHelper && this.gradualDriftHelper < gradualDriftEventsAmountStateTwo) {
                this.gradualDriftHelper ++;
                this.currentGradualDriftEventIo = 0;
                this.currentGradualDriftEventOoo = 0;
            }else if (this.gradualDriftHelper == gradualDriftEventsAmountStateTwo && this.currentGradualDriftEventOoo == this.gradualDriftHelper && this.currentGradualDriftEventIo == this.gradualDriftHelper )
                this.conceptDriftState = 3;
        }

       if (this.currentGradualDriftEventOoo == 0 && this.currentGradualDriftEventIo == 0 && this.gradualDriftEventsAmountStateOne == 0)
           this.conceptDriftState = 2;


       //io events before
       if (videocard.currentEvent <= conceptDriftStartingEvent) {
           delay = 200;
           //delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       //state 1
       else if (this.conceptDriftState == 1 && this.init) {
           if (this.currentGradualDriftEventOoo > 0){
               //oo
               delay = 400;
               //delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
               this.currentGradualDriftEventOoo --;
           }else if (this.currentGradualDriftEventOoo == 0 && this.currentGradualDriftEventIo > 0){
               //io
               delay = 200;
               //delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
               this.currentGradualDriftEventIo --;
           }
       }
       //state 2
       else if (this.conceptDriftState == 2 && !this.init) {
           //io
           if (this.currentGradualDriftEventIo < this.gradualDriftHelper) {
               delay = 200;
               System.out.println(videocard.currentEvent + "Io");
               this.currentGradualDriftEventIo ++;
           }else if (this.currentGradualDriftEventIo == this.gradualDriftHelper && this.currentGradualDriftEventOoo < this.gradualDriftHelper) {
               delay = 400;
               System.out.println(videocard.currentEvent + "Ooo");
               this.currentGradualDriftEventOoo ++;
           }
       }
       //io events after
       else if (videocard.currentEvent >= conceptDriftStartingEvent + videocard.oooEvents) {
           delay = 200;
           //delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       //ooo events
       else {
           delay = 400;
           //delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }

       videocard.currentEvent ++;

        //übergang zum state 1
       if (videocard.currentEvent == conceptDriftStartingEvent) {
           this.conceptDriftState = 1;

       }
        //übergang zum state 2
       if (videocard.currentEvent == conceptDriftStartingEvent + videocard.oooEvents) {
           this.init = false;
           this.currentGradualDriftEventOoo = 0;
           this.currentGradualDriftEventIo = 0;
       }


       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
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
            delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);

        }else {
            delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            if (eventTime.getTime() < this.connectionRecoveryDate.getTime())
                processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);
            else
                processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);

        }
        videocard.currentEvent ++;

        return processingTime;
    }

   public boolean distributionCalculation (Videocard videocard) {
        int result = (int) Math.ceil((Math.random())*(videocard.numberOfEvents-1)+1);
        videocard.numberOfEvents --;
        if (result > videocard.oooEvents) {
           return false;
        }
        else {
            videocard.oooEvents --;
            return true;
        }
   }

   public void stateOneCalculation (Videocard videocard) {
        this.gaussNumber = (int) Math.ceil((videocard.oooEvents*0.10));
        this.gradualDriftEventsAmountStateOne = gaussCalculation(this.gaussNumber);
        this.gradualDriftEventsAmountStateTwo = gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventOoo = this.gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventIo = this.gradualDriftEventsAmountStateOne;

   }

    public int gaussCalculation (int driftAmount) {
        int sum = 0;
        int counter = 1;
        while (sum + counter < driftAmount) {
            sum += counter;
            counter ++;
        }
        return counter;
    }

}

