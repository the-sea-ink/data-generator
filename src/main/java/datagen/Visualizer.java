package datagen;

import javax.swing.JFrame;

import HelperClasses.ConfigReader;
import datagen.Analyzer;
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
        //datagen.Splitter.split();
        if (args.length > 0) {
            source = Integer.parseInt(args[0]);
        }
        else source = 0;

        //scatterChartDelays();
        //histogram(source);
        //scatterChartEventAndProcTimes(source);
        threadPool();

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
                "datagen.Event ID",
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

        XYSeries series1 = new XYSeries("datagen.Event Time");
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
        names[1] = "datagen.Event Time";
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

    public static void threadPool() throws IOException {
        String title = "Name";

        XYSeries firstSeries= new XYSeries("Thread pool");
        firstSeries.add(0, 0);
        firstSeries.add(1, 4.576);
        firstSeries.add(2, 6.604);
        firstSeries.add(3, 8.121);
        firstSeries.add(4, 10.511);
        firstSeries.add(6,15.623);
        firstSeries.add(8,19.422);
        firstSeries.add(10,24.960);

        XYSeries secondSeries= new XYSeries("No thread pool");
        secondSeries.add(0, 0);
        secondSeries.add(1, 4.547);
        secondSeries.add(2, 7.704);
        secondSeries.add(3, 11.291);
        secondSeries.add(4, 13.950);
        secondSeries.add(6,20.164);
        secondSeries.add(8,27.065);
        secondSeries.add(10,33.456);

        XYSeries thirdSeries= new XYSeries("Thread pool no writing");
        thirdSeries.add(0, 0);
        thirdSeries.add(1, 2.396);
        thirdSeries.add(2, 3.922);
        thirdSeries.add(3, 6.078);
        thirdSeries.add(4, 9.929);
        thirdSeries.add(6,13.973);
        thirdSeries.add(8,18.070);
        thirdSeries.add(10,24.024);

        XYSeries fourthSet= new XYSeries("Thread pool no writing");
        fourthSet.add(0, 0);
        fourthSet.add(1, 34.312);
        fourthSet.add(2, 25.108);
        fourthSet.add(3, 22.780);
        fourthSet.add(4, 24.690);
        fourthSet.add(5, 23.841);
        fourthSet.add(6,22.983);
        fourthSet.add(7, 24.136);
        fourthSet.add(8,23.080);
        fourthSet.add(9, 23.087);
        fourthSet.add(10,24.960);
        fourthSet.add(11,23.089);
        fourthSet.add(12,23.951);
        fourthSet.add(13,23.333);
        fourthSet.add(20, 23.300);


        XYSeriesCollection dataset = new XYSeriesCollection();
        //dataset.addSeries(firstSeries);
        //dataset.addSeries(secondSeries);
        //dataset.addSeries(thirdSeries);
        dataset.addSeries(fourthSet);

        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                "Amount of sources",
                "Duration in seconds",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();

        renderer.setDefaultLinesVisible(true);
        renderer.setDefaultShapesFilled(true);
        renderer.setDefaultShapesVisible(true);
        ChartUtils.saveChartAsPNG(new File(title + ".png"), chart, 450, 400);
    }
}
