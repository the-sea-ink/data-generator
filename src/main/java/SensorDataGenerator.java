public class SensorDataGenerator {

    private int numberOfSensors;
    private int currentSensor;
    private int eventID;

    public SensorDataGenerator(int numberOfSensors){
        this.numberOfSensors = numberOfSensors;
        this.currentSensor = 0;
        this.eventID = 0;
    }

    public  int getEventID() {
        this.eventID += 1;
        return eventID;
    }
    public int getSensorID() {
        if (this.currentSensor < this.numberOfSensors) {
            this.currentSensor += 1;
        }
        else {
            this.currentSensor = 1;
        }
        return this.currentSensor;
    };
}
