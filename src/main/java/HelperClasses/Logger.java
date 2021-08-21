package HelperClasses;

import datagen.InsulinSensor;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class Logger {
    Timestamp generationStart;
    Timestamp generationEnd;
    long generationDuration;
    long eventsGenerated;
    int streamDurationInSeconds;
    List<InsulinSensor> sensorList;

    public Logger () {
        this.generationStart = new Timestamp(System.currentTimeMillis());
        System.out.println("-----------------------------------------------------------");
        System.out.println("Starting stream generation...");
    }

    public void setStreamStart () {
        this.generationStart = new Timestamp(System.currentTimeMillis());
    }

    public void setStreamEnd () throws IOException, ParseException {
        this.generationEnd = new Timestamp(System.currentTimeMillis());

        this.setStreamDuration();
    }

    public void setStreamDuration () throws IOException, ParseException {
        this.generationDuration = this.generationEnd.getTime() - this.generationStart.getTime();
        this.eventsGenerated = countGeneratedEvents();
        this.streamDurationInSeconds = setStreamDurationInSeconds();
        this.exportResult();

    }

    private long countGeneratedEvents() {
        long summ = 0;
        for (InsulinSensor sensor : sensorList)
            summ += sensor.currentEvent - 1;
        return summ;
    }

    public int setStreamDurationInSeconds() throws IOException, ParseException {
        return ConfigReader.getStreamDuration();
    }

    public void exportResult () throws IOException, ParseException {
        FileWriter generatorLog = new FileWriter("output/generatorLog.csv", true);
        generatorLog.append((this.streamDurationInSeconds + ", " + this.eventsGenerated + ", "+  this.generationDuration) + System.lineSeparator());
        System.out.println("Stream generation successful.");
        System.out.println("Stream generation took " + this.generationDuration + " milliseconds.");
        System.out.println("-----------------------------------------------------------");
        generatorLog.flush();
    }

    public void setSensors(List<InsulinSensor> sensors) {
        this.sensorList = sensors;
    }
}
