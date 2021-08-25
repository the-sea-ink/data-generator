import HelperClasses.ConfigReader;
import HelperClasses.Event;
import HelperClasses.TimeHandler;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.Date;

public class Delayer {
    int id;
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
    int preGauss = -1;
    int conceptDriftStartingEvent = -1;
    int networkAnomalyDuration;

    public boolean outlier;
    public double oooPercentage;
    public int oooEvents;
    public int ioEvents;
    public int pattern;

    public int amountOfEventsToProcess;

    public Delayer(int id, int amountOfEventsToProcess) throws IOException, ParseException {
        this.id = id;
        if (ConfigReader.getOutlierPattern(this.id ) != -1){
            this.outlier = true;
            this.pattern = ConfigReader.getOutlierPattern(this.id);
        }else{
            this.pattern = ConfigReader.getDelayPattern();
        }

        if (ConfigReader.getOutlierOoo(this.id) != -1 && this.pattern == 1) {
            this.oooPercentage = ConfigReader.getOutlierOoo(this.id);
        }else if ( this.pattern == 1) {
            this.oooPercentage = ConfigReader.getDelayPercentage();
        }

        if (ConfigReader.getOutlierNetworkAnomalyDuration(this.id) ==-1)
            this.networkAnomalyDuration = ConfigReader.getNetworkAnomalyDuration();
        else
            this.networkAnomalyDuration = ConfigReader.getOutlierNetworkAnomalyDuration(this.id);

        if ((this.pattern == 3 || this.pattern == 2) && this.outlier) {
            this.oooPercentage =  (this.networkAnomalyDuration / (double) ConfigReader.getStreamDuration()) * 100;
        }else if (this.pattern == 3 ||  this.pattern == 2) {
            this.oooPercentage =  (this.networkAnomalyDuration / (double)  ConfigReader.getStreamDuration()) * 100;
        }


        this.amountOfEventsToProcess = amountOfEventsToProcess;
        this.oooEvents = (int) Math.ceil((double) amountOfEventsToProcess * (double) oooPercentage / 100);
        this.ioEvents = amountOfEventsToProcess - oooEvents;

    }

    public int delayerRandomDistribution(Boolean ooo) {
       //ooo event
       if (ooo) {
           delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
       }
       //io event
       else {
           delay = (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
       }

       return delay;

   }

    public Event conceptDrift(Event event, InsulinSensor insulinSensor) throws IOException, ParseException
    {
        if (preGauss == -1 && conceptDriftStartingEvent == -1) {
            preGauss = gaussNumberCalc(insulinSensor);
            if (this.ioEvents/2 <= preGauss)
                conceptDriftStartingEvent = 1;
            else {
                conceptDriftStartingEvent = (this.ioEvents/2 - preGauss);
                System.out.println(conceptDriftStartingEvent);
            }

        }

        //übergang zum state 1
        if (insulinSensor.currentEvent == conceptDriftStartingEvent) {
            this.conceptDriftState = 1;

        }
        //übergang zum state 2
        if (insulinSensor.currentEvent == conceptDriftStartingEvent + (this.oooEvents)) {
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
       else if (insulinSensor.currentEvent >= conceptDriftStartingEvent + this.oooEvents) {
           delay = (int) (Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
           //delay = 200;

       }
       //ooo events
       else {
           delay = (int) (Math.random()*(maxDelay - timeBetweenEvents+1)+timeBetweenEvents);
           //delay = 400;

       }

       event.processingTime = TimeHandler.addTimeMilliseconds(event.eventTime, delay);
       //System.out.println(amountOfEventsToProcess);
       amountOfEventsToProcess--;
       return event;
   }

    public Event connectionLoss(Event event, InsulinSensor insulinSensor) throws IOException, ParseException {
        int conceptDriftStartingEvent = this.ioEvents/2;

        //io events before and after
        if (insulinSensor.currentEvent <= conceptDriftStartingEvent || insulinSensor.currentEvent >= conceptDriftStartingEvent + this.oooEvents) {
            this.delay =  (int) Math.floor(Math.random()*(timeBetweenEvents - minDelay+1)+minDelay);
            event.processingTime = TimeHandler.addTimeMilliseconds(event.eventTime, delay);

        }
        //ooo events
        else if (!this.firstConnectionLossCheck) {
            System.out.println(this.id);
            firstConnectionLossCheck = true;
            this.connectionLossDate = event.eventTime;
            this.connectionRecoveryDate = TimeHandler.addTimeMilliseconds(event.eventTime, this.networkAnomalyDuration *1000);
            this.delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            event.processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);
        }else {
            delay = (int) Math.floor(Math.random()*(maxDelay - minDelay+1)+minDelay);
            if (event.eventTime.getTime() < this.connectionRecoveryDate.getTime())
                event.processingTime = TimeHandler.addTimeMilliseconds(this.connectionRecoveryDate, delay);
            else
                event.processingTime = TimeHandler.addTimeMilliseconds(event.eventTime, delay);
        }
        return event;
    }

    public boolean distributionCalculation() {
        int result = (int) Math.ceil((Math.random())*(this.amountOfEventsToProcess));
        this.amountOfEventsToProcess--;
        if (result > oooEvents) {
            return false;
        }
        else {
            oooEvents --;
            return true;
        }
    }

   public void stateOneCalculation (InsulinSensor insulinSensor) {
        this.gaussNumber = (int) Math.ceil((this.oooEvents*0.10));
        this.gradualDriftEventsAmountStateOne = gaussCalculation(this.gaussNumber);
        this.gradualDriftEventsAmountStateTwo = gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventOoo = this.gradualDriftEventsAmountStateOne;
        this.currentGradualDriftEventIo = this.gradualDriftEventsAmountStateOne;
   }

   public int  gaussNumberCalc(InsulinSensor insulinSensor) {
       return (int) Math.ceil((this.oooEvents*0.10));

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

