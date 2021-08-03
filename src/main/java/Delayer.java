import HelperClasses.ConfigReader;
import HelperClasses.TimeHandler;
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

    public  Date delayer(int i, Date eventTime, InsulinSensor insulinSensor, int sourceID) throws IOException, ParseException {
        if (i == 1)
            return delayerRandomDistribution(eventTime, insulinSensor);
        else if (i == 2)
            return delayerConceptDrift(eventTime, insulinSensor);
        else
            return delayerConnectionLoss(eventTime, insulinSensor, sourceID);
    }

    public Date delayerRandomDistribution(Date eventTime, InsulinSensor insulinSensor) throws IOException, ParseException {

       //ooo event
       if (distributionCalculation(insulinSensor)) {
           delay = (int) Math.floor(Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
       }
       //io event
       else {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }
       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;

   }

    public Date delayerConceptDrift(Date eventTime, InsulinSensor insulinSensor) throws IOException, ParseException {
        int preGauss = gaussNumberCalc(insulinSensor);
        int conceptDriftStartingEvent;

        if (insulinSensor.ioEvents/2 <= preGauss)
            conceptDriftStartingEvent = 1;
        else
            conceptDriftStartingEvent = (insulinSensor.ioEvents/2 - preGauss);

        //übergang zum state 1
        if (insulinSensor.currentEvent == conceptDriftStartingEvent) {
            this.conceptDriftState = 1;

        }
        //übergang zum state 2
        if (insulinSensor.currentEvent == conceptDriftStartingEvent + (insulinSensor.oooEvents)) {
            this.init = false;
            this.currentGradualDriftEventOoo = 0;
            this.currentGradualDriftEventIo = 0;
        }


        //check state
       if (conceptDriftState == 1 && !this.init) {
           stateOneCalculation(insulinSensor);
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
       if (insulinSensor.currentEvent <= conceptDriftStartingEvent) {
           delay = (int) (Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
           //delay = 200;

       }
       //state 1
       else if (this.conceptDriftState == 1 && this.init) {
           if (this.currentGradualDriftEventOoo > 0){
               //oo
               delay = (int) (Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
               //delay = 400;
               this.currentGradualDriftEventOoo --;
           }else if (this.currentGradualDriftEventOoo == 0 && this.currentGradualDriftEventIo > 0){
               //io
               delay = (int) (Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
               //delay = 200;
               this.currentGradualDriftEventIo --;
           }
       }
       //state 2
       else if (this.conceptDriftState == 2 && !this.init) {
           //io
           if (this.currentGradualDriftEventIo < this.gradualDriftHelper) {
               delay = (int) (Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
               //delay = 200;

               this.currentGradualDriftEventIo ++;
           }else if (this.currentGradualDriftEventIo == this.gradualDriftHelper && this.currentGradualDriftEventOoo < this.gradualDriftHelper) {
               delay = (int) (Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
               //delay = 400;

               this.currentGradualDriftEventOoo ++;
           }
       }
       //io events after
       else if (insulinSensor.currentEvent >= conceptDriftStartingEvent + insulinSensor.oooEvents) {
           delay = (int) (Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
           //delay = 200;

       }
       //ooo events
       else {
           delay = (int) (Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
           //delay = 400;

       }

       insulinSensor.currentEvent ++;



       Date processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);
       return processingTime;
   }

    public Date delayerConnectionLoss (Date eventTime, InsulinSensor insulinSensor, int sourceID) throws IOException, ParseException {
        int conceptDriftStartingEvent = insulinSensor.ioEvents/2;
        int connectionLoss;
        if (ConfigReader.getDelayPattern() == 3)
            connectionLoss = ConfigReader.getConnectionLossDuration();
        else
            connectionLoss = ConfigReader.getOutlierConnectionLoss(sourceID+1);
        Date processingTime;
        //io events
        if (insulinSensor.currentEvent <= conceptDriftStartingEvent || insulinSensor.currentEvent >= conceptDriftStartingEvent + insulinSensor.oooEvents) {
            delay =  (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
            processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);

        }
        //ooo events
        else if (!firstConnectionLossCheck) {
            firstConnectionLossCheck = true;
            this.connectionLossDate = eventTime;
            this.connectionRecoveryDate = TimeHandler.addTimeMilliseconds(eventTime, connectionLoss*1000);
            delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);

        }else {
            delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            if (eventTime.getTime() < this.connectionRecoveryDate.getTime())
                processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);
            else
                processingTime = TimeHandler.addTimeMilliseconds(eventTime, delay);

        }
        insulinSensor.currentEvent ++;

        return processingTime;
    }

   public boolean distributionCalculation (InsulinSensor insulinSensor) {
        int result = (int) Math.ceil((Math.random())*(insulinSensor.numberOfEvents));
        insulinSensor.numberOfEvents --;
        if (result > insulinSensor.oooEvents) {
           return false;
        }
        else {
            insulinSensor.oooEvents --;
            return true;
        }
   }

   public void stateOneCalculation (InsulinSensor insulinSensor) {
        this.gaussNumber = (int) Math.ceil((insulinSensor.oooEvents*0.10));
        this.gradualDriftEventsAmountStateOne = gaussCalculation(this.gaussNumber);
        this.gradualDriftEventsAmountStateTwo = gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventOoo = this.gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventIo = this.gradualDriftEventsAmountStateOne;
   }

   public int  gaussNumberCalc(InsulinSensor insulinSensor) {
       return (int) Math.ceil((insulinSensor.oooEvents*0.10));
   }

   public int gaussCalculation (int driftAmount) {
        int sum = 0;
        int counter = 1;
        while (sum + counter < driftAmount) {
            if (sum + counter + (counter+1) > driftAmount)
                break;
            sum += counter;
            counter ++;
        }
        return counter;
    }

}

