import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Analyzer {
    //for statistics

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        int eventCount = getTotalTransactions();
        int streamDuration = getDurationInMilliseconds();

        System.out.println("-----------------------------------------------------------");
        System.out.println("Stream duration: " + streamDuration + " milliseconds");
        System.out.println("Amount of events: " + eventCount);
        System.out.println("-----------------------------------------------------------");

    }
    public static int getTotalTransactions() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        int count = 0;
        while((br.readLine()) != null)
            count++;
        return count;

    }
    public static int getDurationInMilliseconds() throws IOException, ParseException, java.text.ParseException {
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));
        //get first line
        String firstLine = br.readLine();
        String[] firstLineArray = firstLine.split(",");
        int eventTimeColumn = ConfigReader.getEventTimeColumn()-1;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String startingTime = firstLineArray[eventTimeColumn];
        Date dateStart = sdf.parse(startingTime);
        Timestamp tsStart = new Timestamp(dateStart.getTime());

        //get last line
        String lastLine = "";
        while((br.readLine()) != null)
            lastLine = br.readLine();
        String[] lastLineArray = lastLine.split(",");
        String endTime = lastLineArray[eventTimeColumn];
        Date dateEnd = sdf.parse(endTime);
        Timestamp tsEnd = new Timestamp(dateEnd.getTime());

        Date streamDurationDate = new Date (dateEnd.getTime() - dateStart.getTime());
        int streamDurationMilliseconds = (int) (streamDurationDate.getTime());

        return streamDurationMilliseconds;
    }
    public static void statProvider () {}



}
