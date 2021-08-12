import HelperClasses.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataGen {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException, InterruptedException {

        Logger log = new Logger();
        Stream dataStream = new Stream();

        int amountOfSources = ConfigReader.getAmountOfSources();
        String outputFile = "output/output.csv";
        new File(outputFile).delete();

        List<Generator> generators = new ArrayList<>();

        for (int i = 1; i < amountOfSources+1; i++) {
            InsulinSensor newSensor = new InsulinSensor(i);
            Delayer newDelayer = new Delayer(i, newSensor.amountOfEventsToGenerate);
            generators.add(new Generator(newSensor, newDelayer, outputFile));
        }

        boolean multithreaded = false;

        long startTime = System.nanoTime();

        if (multithreaded){
            List<Thread> threads = new ArrayList<>();
            for(Generator gen : generators){
                Thread thread = new Thread(gen);
                threads.add(thread);
                thread.setPriority(10);
                thread.start();
            }
            for (Thread thread : threads)
                thread.join();
        } else {
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
        }
        long endTime = System.nanoTime();
        //System.out.println("duration " + Long.toString((endTime - startTime)/1000000));

        log.setStreamEnd();
    }



}


