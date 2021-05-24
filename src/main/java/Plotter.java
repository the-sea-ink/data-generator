import javax.swing.JFrame;

import HelperClasses.ConfigReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.ChartUtils;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;


public class Plotter extends JFrame {


    public static void main(String[] args) throws IOException, ParseException {
        lineChart();
        histogram();

    }

    public static void lineChart () throws IOException, ParseException {
        //read output
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = new Date(Long.parseLong(eventTimeString));

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        int i = 1;

        XYSeries series1 = new XYSeries("Data");

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));


            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();

            series1.add(i,diff);
            i++;

        }

        //series1.add(1, 20);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "RandomDistribution",
                "Event ID",
                "Delay",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );


        ChartUtils.saveChartAsPNG(new File("ConceptDrift.png"), chart, 450, 400);
    }

    public static void histogram() throws IOException, ParseException {
        int size = Analyzer.getTotalEvents();
        double[] delays = new double[size-1];

        //read output
        BufferedReader br = new BufferedReader(new FileReader("output/output.csv"));

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = new Date(Long.parseLong(eventTimeString));

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        int i = 0;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));


            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();

            delays[i] = (double) diff;
            i++;

        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(ConfigReader.getDelayPercentage() + "% out-of-order", delays, 50);

        JFreeChart histogram = ChartFactory.createHistogram("Processing time delay in milliseconds - distribution",
                "delays", "frequency", dataset);

        ChartUtils.saveChartAsPNG(new File(ConfigReader.getDelayPercentage() + "% out-of-order histogram.png"), histogram, 450, 400);
    }
}
