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
    public double sensorValue;
    public boolean sensorWarning;

    public static int eventIDColumn = -1;
    public static int eventTimeColumn = -1;
    public static int processingTimeColumn = -1;
    public static int criticalPointColumn = -1;
    public static int sensorValueColumn = -1;

    public Event(Date eventTime, InsulinSensor sensor, double sensorValue, boolean sensorWarning) {
        this.eventTime = eventTime;
        this.sensor = sensor;
        this.sensorValue = sensorValue;
        this.sensorWarning = sensorWarning;
    }

    public static Event parseFromString(String line) throws IOException, ParseException {
        if (Event.eventTimeColumn == -1){
            Event.eventIDColumn = -1;
            Event.eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
            Event.processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;
            Event.criticalPointColumn = ConfigReader.getCriticalPointColumn() -1;
            //sensorValueColumn = ConfigReader.getSensorValueColumn() -1;
        }


        String[] lineArray;
        lineArray = line.split(",");
        String eventTimeString = lineArray[Event.eventTimeColumn];
        String prevProcessingTimeString = lineArray[Event.processingTimeColumn];
        String criticalPointColumn = lineArray[Event.criticalPointColumn];

        Date eventTimeDate = new Date(Long.parseLong(eventTimeString));
        Date prevProcessingTimeDate = new Date(Long.parseLong(prevProcessingTimeString));
        boolean sensorWarning = Boolean.parseBoolean(criticalPointColumn);
        Event newEvent = new Event(eventTimeDate, null, -1, sensorWarning);
        newEvent.processingTime = prevProcessingTimeDate;
        return newEvent;
    }

    @Override
    public String toString() {
        return id +"," + eventTime.getTime() + "," + processingTime.getTime() + "," + sensor.id + "," + sensorValue + "," + sensorWarning + ",\n";
    }
}
