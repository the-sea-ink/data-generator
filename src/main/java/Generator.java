import HelperClasses.TimeHandler;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Generator implements Runnable {

    InsulinSensor inputSensor;
    Delayer delayer;
    FileWriter csvWriter;

    public Generator(InsulinSensor inputSensor, Delayer delayer,  String outputFile) throws IOException {
        this.inputSensor = inputSensor;
        this.delayer = delayer;
        csvWriter = new FileWriter(outputFile, true);
    }

    private void generateRandomDistribution() throws IOException {

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

            for (Event delayedEvent : oooEvents){
                csvWriter.write(delayedEvent.toString());
            }
            csvWriter.write(currentEvent.toString());

            oooEvents.clear();
        }
        csvWriter.flush();
    }

    private void generateConceptDrift() throws IOException, ParseException {
        while(!inputSensor.finished()){
            Event currentEvent = inputSensor.generateNewEvent();
            delayer.conceptDrift(currentEvent, inputSensor);
            csvWriter.write(currentEvent.toString());
        }
        csvWriter.flush();
    }

    private void generateConnectionLost() throws IOException, ParseException {
        while(!inputSensor.finished()){
            Event currentEvent = inputSensor.generateNewEvent();
            delayer.connectionLoss(currentEvent, inputSensor);
            csvWriter.write(currentEvent.toString());
        }
        csvWriter.flush();
    }

    @Override
    public void run() {
        switch (delayer.pattern){
            case (1):
                try {
                    generateRandomDistribution();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
