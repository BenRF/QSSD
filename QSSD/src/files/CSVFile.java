package files;

import parse.ParseTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVFile extends TabSeperatedFile {
    ArrayList<ArrayList<Object>> content;
    public CSVFile(String fileName) {
        this.content = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(fileName));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                ArrayList<Object> r = new ArrayList<>();
                r.addAll(Arrays.asList(data));
                this.content.add(r);
            }
            csvReader.close();
        } catch (Exception e) {
            System.out.println("FILE NOT FOUND");
        }
    }

    @Override
    public ArrayList<ParseTable> getTables() {
        ArrayList<ParseTable> tables = new ArrayList<>();
        ParseTable pT = new ParseTable(this.content);
        tables.add(pT);
        return tables;
    }
}
