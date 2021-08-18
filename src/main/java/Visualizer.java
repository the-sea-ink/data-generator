import javax.swing.JFrame;

import HelperClasses.ConfigReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtils;
import org.json.simple.parser.ParseException;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Visualizer extends JFrame {

    public static int source = 0;

    public static void main(String[] args) throws IOException, ParseException {
        Splitter.split();
        if (args.length > 0) {
            source = Integer.parseInt(args[0]);
        }
        else source = 0;

        scatterChartDelays();
        histogram(source);
        scatterChartEventAndProcTimes(source);

    }

    public static void scatterChartDelays() throws IOException, ParseException {
        //read output
        int sourcesAmount = ConfigReader.getAmountOfSources();

        String title = "";
        if (ConfigReader.getDelayPattern() == 1)
            title = "Random Distribution";
        else if (ConfigReader.getDelayPattern() == 2)
            title = "Concept Drift";
        else
            title = "Connection Loss";

        List<XYSeries> dataSeries  = new ArrayList<>();
        for (int i = 1; i <= sourcesAmount; i++) {
            dataSeries.add(new XYSeries(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int currentSource = 0; currentSource < sourcesAmount; currentSource ++) {
            String currentInputFile = "output/output" + currentSource +".csv";

            BufferedReader br = new BufferedReader(new FileReader(currentInputFile));

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

            while ((line = br.readLine()) != null) {

                lineArray = line.split(",");

                eventTimeString = lineArray[eventTimeColumn];
                evenTimeDate = new Date(Long.parseLong(eventTimeString));

                processingTimeString = lineArray[processingTimeColumn];
                processingTimeDate = new Date(Long.parseLong(processingTimeString));


                Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
                Timestamp tsET = new Timestamp(evenTimeDate.getTime());
                long diff = tsPT.getTime() - tsET.getTime();

                dataSeries.get(currentSource).add(i,diff);
                i++;

            }


            dataset.addSeries(dataSeries.get(currentSource));

        }


        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                "Event ID",
                "Delay",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        chart.getXYPlot().getRenderer().setSeriesShape( 0, new Rectangle2D.Double( -1.0, -1.0, 1.0, 5.0 ) );
        chart.getXYPlot().getRenderer().setSeriesShape( 1, new Rectangle2D.Double( -1.0, -1.0, 1.0, 5.0 ) );

        ChartUtils.saveChartAsPNG(new File(title + ".png"), chart, 450, 400);
    }

    public static void histogram(int source) throws IOException, ParseException {
        String inputFile = "output/output.csv";
        if (source != 0)
            inputFile = "output/output" + source + ".csv";
        int size = Analyzer.getTotalEvents(inputFile);
        double[] delays = new double[size-1];

        //read output
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        if (inputFile.equals("output/output.csv"))
            br.readLine();

        //init event/processing time columns/values
        int eventTimeColumn = ConfigReader.getEventTimeColumn() - 1;
        int processingTimeColumn = ConfigReader.getProcessingTimeColumn() - 1;

        String line = br.readLine();
        String[] lineArray = line.split(",");

        String eventTimeString = lineArray[eventTimeColumn];
        Date evenTimeDate = new Date(Long.parseLong(eventTimeString));

        String processingTimeString = lineArray[processingTimeColumn];
        Date processingTimeDate = new Date(Long.parseLong(processingTimeString));

        int lineNum = 0;

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));


            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();

            delays[lineNum] = (double) diff;
            lineNum++;

        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(ConfigReader.getOutlierOoo(source) + "% out-of-order", delays, 50);

        JFreeChart histogram = ChartFactory.createHistogram("Distribution of processing time delay in milliseconds",
                "delay", "frequency", dataset);

        ChartUtils.saveChartAsPNG(new File(ConfigReader.getOutlierOoo(source) + "% out-of-order histogram.png"), histogram, 450, 400);
    }

    public static void scatterChartEventAndProcTimes (int source) throws IOException, ParseException {

        String title;
        if (ConfigReader.getDelayPattern() == 1)
            title = "Scatter Chart - Random Distribution";
        else if (ConfigReader.getDelayPattern() == 2)
            title = "Scatter Chart - Concept Drift";
        else
            title = "Scatter Chart - Connection Loss";


        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series1 = new XYSeries("Event Time");
        XYSeries series2 = new XYSeries("Processing Time");

        String currentInputFile = "output/output" + source +".csv";

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "Seconds",
                "Value",
                dataset,
                false,
                false,
                false);

        chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(false, true));

        String axisName = "";
        String[] names = new String[2];
        names[0] = "Processing Time";
        names[1] = "Event Time";
        SymbolAxis axis = new SymbolAxis(axisName, names);
        chart.getXYPlot().setRangeAxis(axis);

        chart.getXYPlot().getRenderer().setSeriesShape( 0, new Rectangle2D.Double( -1.0, -1.0, 1.0, 5.0 ) );
        chart.getXYPlot().getRenderer().setSeriesShape( 1, new Rectangle2D.Double( -1.0, -1.0, 1.0, 5.0 ) );

        BufferedReader br = new BufferedReader(new FileReader(currentInputFile));

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

        while ((line = br.readLine()) != null) {

            lineArray = line.split(",");

            eventTimeString = lineArray[eventTimeColumn];
            evenTimeDate = new Date(Long.parseLong(eventTimeString));

            processingTimeString = lineArray[processingTimeColumn];
            processingTimeDate = new Date(Long.parseLong(processingTimeString));

            Timestamp tsPT = new Timestamp(processingTimeDate.getTime());
            Timestamp tsET = new Timestamp(evenTimeDate.getTime());
            long diff = tsPT.getTime() - tsET.getTime();

            series2.add(processingTimeDate.getTime(), 0);
            series1.add(evenTimeDate.getTime(), 1);


            i++;

        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        ChartUtils.saveChartAsPNG(new File(title + ".png"), chart, 450, 400);

    }
}
