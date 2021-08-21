package datagen;

import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;

public class Event {

    public int id;
    public Date eventTime;
    public Date processingTime;
    public boolean ooo = false;
    public int delayPattern;
    public InsulinSensor sensor;
    public int sensorId;
    public double sensorValue;
    public boolean sensorWarning;

    public static int eventIDColumn = -1;
    public static int eventTimeColumn = -1;
    public static int processingTimeColumn = -1;
    public static int criticalPointColumn = -1;
    public static int sensorValueColumn = -1;
    public static int sensorIDColumn = -1;

    public Event(Date eventTime, InsulinSensor sensor, double sensorValue, boolean sensorWarning) {
        this.eventTime = eventTime;
        this.sensor = sensor;
        if (sensor!= null)
            this.sensorId = sensor.id;
        this.sensorValue = sensorValue;
        this.sensorWarning = sensorWarning;
    }

    public static Event parseFromString(String line) throws IOException, ParseException {
        if (Event.eventTimeColumn == -1){
            Event.eventIDColumn = 0;
            Event.eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
            Event.processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
            Event.sensorIDColumn = 3;
            Event.sensorValueColumn = 4;
            Event.criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;
        }

        String[] lineArray;
        lineArray = line.split(",");
        String evenID = lineArray[Event.eventIDColumn];
        String eventTimeString = lineArray[Event.eventTimeColumn];
        String prevProcessingTimeString = lineArray[Event.processingTimeColumn];
        String sensorIDString = lineArray[Event.sensorIDColumn];
        String sensorValueString = lineArray[Event.sensorValueColumn];
        String criticalPointColumn = lineArray[Event.criticalPointColumn];

        Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
        Date prevProcessingTimeDate = new Date(Long.parseLong(prevProcessingTimeString));
        boolean sensorWarning = Boolean.parseBoolean(criticalPointColumn);
        Event newEvent = new Event(eventTimeDate, null, -1, sensorWarning);
        newEvent.id = Integer.parseInt(evenID);
        newEvent.eventTime = eventTimeDate;
        newEvent.processingTime = prevProcessingTimeDate;
        newEvent.sensorId = Integer.parseInt(sensorIDString);
        newEvent.sensorValue = Double.parseDouble(sensorValueString);
        newEvent.sensorWarning = Boolean.parseBoolean(criticalPointColumn);
        return newEvent;
    }

    @Override
    public String toString() {
        return id +"," + eventTime.getTime() + "," + processingTime.getTime() + "," + sensorId + "," + sensorValue + "," + sensorWarning + ",\n";
    }
}
