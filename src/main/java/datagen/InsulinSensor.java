package datagen;

import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;

public class InsulinSensor {

    public int serialNumber;
    public double value;
    public boolean highGluckoseWarning;
    public double glucoseWarningAmount;
    public int highestGluckoseAmount;
    public int oversugared = 0;

    public long amountOfEventsToGenerate;
    public int currentEvent = 1;
    public int eventIDOutputFile = 1;
    public int id;


    private Date currentTime;
    private int timeBetweenEvents;

    private int amountOfSensors;

    public InsulinSensor(int id) throws IOException, ParseException {
        this.value = 4.0;
        this.highGluckoseWarning = false;
        this.glucoseWarningAmount = 13.8;
        this.highestGluckoseAmount = 15;
        this.id = id;
        this.serialNumber = this.id +1;
        long streamDuration = ConfigReader.getStreamDuration() * 1000;
        amountOfEventsToGenerate =  Math.round(streamDuration/(double) ConfigReader.getTimeBetweenTransactions());

        eventIDOutputFile = this.id;
        try {
            currentTime = ConfigReader.getStartingTime();
            timeBetweenEvents = ConfigReader.getTimeBetweenTransactions();
            amountOfSensors = ConfigReader.getAmountOfSources();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateValues() {
        glucoseUpdater();
        lowGlucoseUpdater();
    }


    public double glucoseRandomizer() {
        return  (Math.random() + 0.9);
    }

    public void glucoseUpdater() {
        if ((glucoseRandomizer()<1.5 || this.value <=4) && this.value < this.highestGluckoseAmount  && this.oversugared < 2) {
            this.value += glucoseRandomizer();
            this.value = Math.floor(this.value * 100) / 100;
        }else {
            this.value -= glucoseRandomizer();
            this.value = Math.floor(this.value * 100) / 100;
        }
        if (this.value >= this.glucoseWarningAmount) {
            this.oversugared ++;
        } else {
            this.oversugared = 0;
        }
    }



    public void lowGlucoseUpdater() {
        if (this.value >= this.glucoseWarningAmount)
            highGluckoseWarning = true;
        else
            highGluckoseWarning = false;
    }

    public Event generateNewEvent() {
        Event newEvent = new Event(currentTime,this, value, highGluckoseWarning);
        currentTime = new Date (this.currentTime.getTime() + timeBetweenEvents);
        newEvent.id = eventIDOutputFile;
        eventIDOutputFile = eventIDOutputFile + amountOfSensors;
        currentEvent++;
        amountOfEventsToGenerate--;
        updateValues();
        return newEvent;
    }

    public boolean finished(){
        if (this.amountOfEventsToGenerate == 0) {
            return true;
        }
        return false;

    }
}
