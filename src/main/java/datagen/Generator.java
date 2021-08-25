package datagen;

import HelperClasses.CsvQueueWriter;
import HelperClasses.Event;
import HelperClasses.TimeHandler;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Generator implements Runnable {

    InsulinSensor inputSensor;
    Delayer delayer;
    CsvQueueWriter csvWriter;

    public Generator(InsulinSensor inputSensor, Delayer delayer,  CsvQueueWriter csvWriter) throws IOException {
        this.inputSensor = inputSensor;
        this.delayer = delayer;
        this.csvWriter = csvWriter;
    }

    private void generateRandomDistribution() {

        List<Event> oooEvents = new ArrayList<>();
        while(!inputSensor.finished()){
            Event currentEvent = inputSensor.generateNewEvent();
            boolean ooo = delayer.distributionCalculation();
            if (ooo){
                oooEvents.add(currentEvent);
                continue;
            }

            int delay = delayer.delayerRandomDistribution(false);
            currentEvent.processingTime = TimeHandler.addTimeMilliseconds(currentEvent.eventTime, delay);

            for (Event delayedEvent : oooEvents){
                delay = delayer.delayerRandomDistribution(true);
                delayedEvent.processingTime =  TimeHandler.addTimeMilliseconds(currentEvent.processingTime, delay);
            }

            List<String> eventStrings = new ArrayList<>();
            for (Event event : oooEvents){
                eventStrings.add(event.toString());
            }
            csvWriter.queueEventList(eventStrings);

            csvWriter.queueEvent(currentEvent.toString());

            oooEvents.clear();
        }
        csvWriter.notifySensorIsDone();
    }

    private void generateConceptDrift() throws IOException, ParseException {
        while(!inputSensor.finished()){
            Event currentEvent = inputSensor.generateNewEvent();
            delayer.conceptDrift(currentEvent, inputSensor);
            csvWriter.queueEvent(currentEvent.toString());
        }
        csvWriter.notifySensorIsDone();
    }

    private void generateConnectionLost() throws IOException, ParseException {
        while(!inputSensor.finished()){
            Event currentEvent = inputSensor.generateNewEvent();
            delayer.connectionLoss(currentEvent, inputSensor);
            csvWriter.queueEvent(currentEvent.toString());
        }
        csvWriter.notifySensorIsDone();
    }

    @Override
    public void run() {
        switch (delayer.pattern){
            case (1):
                generateRandomDistribution();
                break;
            case (2):
                try {
                    generateConceptDrift();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case(3):
                try {
                    generateConnectionLost();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        System.out.println("done " + inputSensor.id);
    }
}
