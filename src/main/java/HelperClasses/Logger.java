package HelperClasses;

import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class Logger {
    Timestamp generationStart;
    Timestamp generationEnd;
    long generationDuration;
    int eventsGenerated;
    int streamDurationInSeconds;

    public Logger () {
        this.generationStart = new Timestamp(System.currentTimeMillis());

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
        this.eventsGenerated = setGeneratedEvents();
        this.streamDurationInSeconds = setStreamDurationInSeconds();
        this.exportResult();

    }

    public int setStreamDurationInSeconds() throws IOException, ParseException {
        return ConfigReader.getRuntime();
    }
    public int setGeneratedEvents () throws IOException, ParseException {
        return ConfigReader.getRuntime()*1000/ConfigReader.getTimeBetweenTransactions()*ConfigReader.getAmountOfSources() + ConfigReader.getAmountOfSources();
    }

    public void exportResult () throws IOException, ParseException {
        FileWriter generatorLog = new FileWriter("output/generatorLog.csv", true);
        generatorLog.append((this.streamDurationInSeconds + ", " + this.eventsGenerated + ", "+  this.generationDuration) + System.lineSeparator());
        generatorLog.flush();

    }
}
