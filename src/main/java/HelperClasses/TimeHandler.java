package HelperClasses;
import java.util.Date;


public class TimeHandler {
    
    public static Date addTimeMilliseconds(Date date, int time) {
        Date newDate = new Date (date.getTime() + time);
        return newDate;
    }

    public static Date addTimeSeconds (Date date, int time) {
        Date newDate = new Date (date.getTime() + time*1000);
        return newDate;
    }
}
