package files;

import parse.ParseTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVFile implements TabSeperatedFile {
    private ArrayList<ArrayList<Object>> content;
    public CSVFile(String fileName) {
        this.content = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(fileName));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                ArrayList<Object> r = new ArrayList<>(Arrays.asList(data));
                this.content.add(r);
            }
            csvReader.close();
        } catch (Exception e) {
            System.out.println("FILE NOT FOUND");
        }
    }

    public CSVFile(ParseTable pt,String fileName) {
        try {
            FileWriter fW = new FileWriter(fileName + ".csv");
            boolean first = true;
            for (String h: pt.getHeaderNames()) {
                if (!first) {
                    fW.append(",");
                } else {
                    first = false;
                }
                fW.append(h);
            }
            fW.append("\n");
            for (int r = 0; r < pt.rowCount(); r++) {
                ArrayList<Object> row = pt.getRow(r);
                first = true;
                for (Object c: row) {
                    if (first) {
                        first = false;
                    } else {
                        fW.append(",");
                    }
                    fW.append(c.toString());
                }
                fW.append("\n");
            }
            fW.flush();
            fW.close();
            System.out.println("DONE");
        } catch (Exception e) {
            System.out.println("ERROR WRITING FILE");
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
