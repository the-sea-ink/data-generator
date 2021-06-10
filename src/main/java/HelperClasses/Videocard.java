package HelperClasses;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Random;

public class Videocard {

    public int serialNumber;
    public double temperature;
    public boolean overheatWarning;
    public int overheatWarningTemperature;

    public int oooPercentage;
    public int numberOfEvents;
    public int oooEvents;
    public int ioEvents;
    public int currentEvent = 1;
    public int id;

    public Videocard(int id) throws IOException, ParseException {
        this.serialNumber = randomSerialNumberGenerator();
        this.temperature = 40;
        this.overheatWarning = false;
        this.overheatWarningTemperature = 50;
        this.id = id;
        if (ConfigReader.getExceptionSource() && this.id == ConfigReader.getExceptionSourceNumber()) {
            this.oooPercentage = ConfigReader.getExceptionSourceDelay();
        }
        else
            this.oooPercentage = ConfigReader.getDelayPercentage();
        numberOfEvents = (ConfigReader.getRuntime()*1000 / ConfigReader.getTimeBetweenTransactions())+1;
        oooEvents = (int) Math.ceil(numberOfEvents * oooPercentage / 100);
        ioEvents = numberOfEvents - oooEvents;
    }

    public void Updater() {
        temperatureUpdater();
        overheatUpdater();
    }

    public int randomSerialNumberGenerator () {
        Random randomNumber = new Random();
        int random = randomNumber.nextInt(999999);
        return random;
    }

    public int temperatureRandomizer () {
        return (int) (Math.random() + 1.5 );
    }

    public void temperatureUpdater () {
        if ((temperatureRandomizer()<2 || this.temperature<=30) && this.temperature < 70 ) {
            this.temperature += temperatureRandomizer();
        }else {
            this.temperature -= temperatureRandomizer();
        }
    }

    public void overheatUpdater () {
        if (this.temperature >= 50)
            overheatWarning = true;
        else
            overheatWarning = false;
    }
}
