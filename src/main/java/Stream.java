import HelperClasses.ConfigReader;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;

public class Stream {
    private int eventID = 0;
    private Date startingTime = ConfigReader.getStartingTime();
    private Date currentEventTime = ConfigReader.getStartingTime();

    public Stream() throws ParseException, java.text.ParseException, IOException {
    }

    public Integer getEventID () {
        this.eventID += 1;
        return this.eventID;
    }

    public Date getStartingTime() throws ParseException, java.text.ParseException, IOException {
        this.startingTime = ConfigReader.getStartingTime();
        return startingTime;
    }

    public Date getCurrentEventTime () {
        return this.currentEventTime;
    }

    public void timeUpdater () throws IOException, ParseException {
        Date newTime = new Date (this.currentEventTime.getTime() + ConfigReader.getTimeBetweenTransactions());
        this.currentEventTime = newTime;
    }
}
