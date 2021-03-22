
import HelperClasses.Exporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class Generator {

    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<>();

        Exporter.listGenerator(list, "1");
        Exporter.listGenerator(list, "2");

        String string = Exporter.stringGenerator(list);

        FileWriter fileWriter = Exporter.exporterInit();
        Exporter.exporter(fileWriter, string);

    }



}


