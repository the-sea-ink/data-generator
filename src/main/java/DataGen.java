import HelperClasses.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DataGen {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException, InterruptedException {

        Logger log = new Logger();

        int amountOfSources = ConfigReader.getAmountOfSources();
        String outputFile = ConfigReader.getOutputFile();
        CsvQueueWriter csvWriter = new CsvQueueWriter(outputFile, amountOfSources);

        List<Generator> generators = new ArrayList<>();
        Thread writingThread = new Thread(csvWriter);
        writingThread.setPriority(Thread.MAX_PRIORITY);
        for (int i = 1; i < amountOfSources+1; i++) {
            InsulinSensor newSensor = new InsulinSensor(i);
            Delayer newDelayer = new Delayer(i, newSensor.amountOfEventsToGenerate);
            generators.add(new Generator(newSensor, newDelayer, csvWriter));
        }
<<<<<<< HEAD:src/main/java/datagen/DataGen.java
        log.setSensors(sensors);
=======

>>>>>>> parent of 3b2448f (package added):src/main/java/DataGen.java
        boolean multithreaded = true;

        long startTime = System.nanoTime();
        if (multithreaded){
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            executor.execute(csvWriter);
            for(Generator gen : generators){
                executor.execute(gen);
            }
            executor.shutdown();
        } else {
            writingThread.start();
            Thread thread = new Thread(){
                @Override
                public void run() {
                    for(Generator gen : generators){
                        gen.run();
                    }
                }
            };
            thread.start();
            thread.join();
            writingThread.join();
        }

        long endTime = System.nanoTime();

        log.setStreamEnd();
    }



}


