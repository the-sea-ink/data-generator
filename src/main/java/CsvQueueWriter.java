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
    //private ConcurrentLinkedQueue<Event> eventsToWrite;
    private ConcurrentLinkedQueue<String> eventsToWrite;
    private AtomicInteger activeSensors;

    public CsvQueueWriter(String outputFile, Integer activeSensors) {
        new File(outputFile).delete();
        try {
            //this.csvWriter = new FileWriter(outputFile, true);
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

    /*public void queueEvent(Event event){
        this.eventsToWrite.add(event);
    }

    public void queueEvents(List<Event> events){
        for (Event event : events){
            this.eventsToWrite.add(event);
        }
    }*/

    public void queueEventsStrings(List<String> events){
        for (String event : events){
            this.eventsToWrite.add(event);
        }
    }

    public void queueEventStrings(String event){
        this.eventsToWrite.add(event);
    }

    @Override
    public void run() {
        while(true){
            while(!eventsToWrite.isEmpty()){
                try {
                    csvWriter.write(eventsToWrite.poll());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (activeSensors.get() == 0) {
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
