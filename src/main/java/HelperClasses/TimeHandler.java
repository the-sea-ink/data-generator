package HelperClasses;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;


public class TimeHandler {

    public static String timeConverter () throws IOException, ParseException, java.text.ParseException {
        //Timestamp ts = new Timestamp(System.currentTimeMillis());
        //Date date=ts;

        Date date = ConfigReader.getStartingTime();
        System.out.println(date);

        Date d = new Date(date.getTime() + 1000);
        System.out.println(d);
        Timestamp ts = new Timestamp(date.getTime());
        System.out.println(ts);
        return "1223";
    }

    public static Date addTimeMilliseconds(Date date, int time) {
        Date newDate = new Date (date.getTime() + time);
        return newDate;
    }

    public static Date addTimeSeconds (Date date, int time) {
        Date newDate = new Date (date.getTime() + time*1000);
        return newDate;
    }
}
