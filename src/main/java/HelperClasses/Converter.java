package HelperClasses;

import java.util.List;

public class Converter {

    public static <String> List<String> listGenerator(List<String> list, String item) {
        list.add(item);
        return list;
    }

    public static String stringGenerator(List<String>stringList){

        String dataString = "";
        for (String element : stringList) {
            dataString += element + ",";
        }
        dataString = dataString + System.lineSeparator();
        return dataString;
    }
}
