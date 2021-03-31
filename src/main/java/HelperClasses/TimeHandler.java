package HelperClasses;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

public class TimeHandler {

    public static String timeConverter () {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date=ts;
        System.out.println(date);
        return "1223";
    }

}
