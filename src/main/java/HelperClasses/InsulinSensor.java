package HelperClasses;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class InsulinSensor {

    public int serialNumber;
    public double glucoseAmount;
    public boolean highGluckoseWarning;
    public int glucoseWarningAmount;
    public int highestGluckoseAmount;

    public boolean outlier;
    public int oooPercentage;
    public int numberOfEvents;
    public int oooEvents;
    public int ioEvents;
    public int currentEvent = 1;
    public int id;
    public int pattern;

    public InsulinSensor(int id) throws IOException, ParseException {
        this.glucoseAmount = 5.0;
        this.highGluckoseWarning = false;
        this.glucoseWarningAmount = 20;
        this.highestGluckoseAmount = 22;
        this.id = id;
        this.serialNumber = this.id +1;
        if (ConfigReader.getOutlierOoo(this.id+1) != -1) {
            this.oooPercentage = ConfigReader.getOutlierOoo(this.id+1);
        }else {
            this.oooPercentage = ConfigReader.getDelayPercentage();
        }
        if (ConfigReader.getOutlierPattern(this.id +1) != -1){
            this.pattern = ConfigReader.getOutlierPattern(this.id +1);
        }else{
            this.pattern = ConfigReader.getDelayPattern();
        }
        numberOfEvents = (ConfigReader.getRuntime()*1000 / ConfigReader.getTimeBetweenTransactions())+1;
        oooEvents = (int) Math.ceil((double) numberOfEvents * (double) oooPercentage / 100);
        ioEvents = numberOfEvents - oooEvents;
    }

    public void Updater() {
        glucoseUpdater();
        lowGlucoseUpdater();
    }


    public double glucoseRandomizer() {
        return  (Math.random() + 1.5);
    }

    public void glucoseUpdater() {
        if ((glucoseRandomizer()<2 || this.glucoseAmount <=5) && this.glucoseAmount < this.highestGluckoseAmount ) {
            this.glucoseAmount += glucoseRandomizer();
            this.glucoseAmount = Math.floor(this.glucoseAmount * 100) / 100;
        }else {
            this.glucoseAmount -= glucoseRandomizer();
            this.glucoseAmount = Math.floor(this.glucoseAmount * 100) / 100;
        }
    }

    public void lowGlucoseUpdater() {
        if (this.glucoseAmount >= this.highestGluckoseAmount)
            highGluckoseWarning = true;
        else
            highGluckoseWarning = false;
    }
}
