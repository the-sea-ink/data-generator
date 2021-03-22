package HelperClasses;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Exporter {

    public static <String> List<String> listGenerator(List<String> list, String item) {
        list.add(item);
        return list;
    }

    public static String stringGenerator(List<String>stringList){

        String dataString = "";
        for (String element : stringList) {
            dataString += element + ",";
        }
        dataString += "\n";
        return dataString;
    }

    public static FileWriter exporterInit() throws IOException {
        FileWriter csvWriter = new FileWriter("output/output.csv");
        return csvWriter;
    }

    public static void exporter(FileWriter csvWriter, String dataString) throws IOException {
        csvWriter.append(dataString);
        csvWriter.flush();
        csvWriter.close();
    }

}
