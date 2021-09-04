package datagen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedWriter;

public class CsvQueueWriter implements Runnable {

    //FileWriter csvWriter;
    BufferedWriter csvWriter;
    private ConcurrentLinkedQueue<String> eventsToWrite;
    private AtomicInteger activeSensors;

    public CsvQueueWriter(String outputFilePath, Integer activeSensors) {
        File outputFile = new File(outputFilePath);
        outputFile.mkdirs();
        if (outputFile.exists())
            outputFile.delete();
        try {
            FileWriter file = new FileWriter(outputFile);
            this.csvWriter = new BufferedWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.activeSensors = new AtomicInteger(activeSensors);
        this.eventsToWrite = new ConcurrentLinkedQueue<String>();
    }

    public void notifySensorIsDone(){
        this.activeSensors.decrementAndGet();
    }


    public void queueEventList(List<String> events){
        for (String event : events){
            this.eventsToWrite.add(event);
        }
    }

    public void queueEvent(String event){
        this.eventsToWrite.add(event);
    }

    @Override
    public void run() {
        try {
            csvWriter.write("eventID, eventTime, processingTime, sensorID, sensorValue, sensorWarning" + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            while(!eventsToWrite.isEmpty()){
                try {
                    csvWriter.write(eventsToWrite.poll());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (activeSensors.get() == 0 && eventsToWrite.isEmpty()) {
                try {
                    csvWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}
