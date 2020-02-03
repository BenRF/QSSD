package files;

import parse.ParseTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class CSVFile extends TabSeperatedFile {
    private String fileName;

    public CSVFile(String fileName) {
        this.fileName = fileName;
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
            for (int r = 0; r < pt.getRowCount(); r++) {
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
    public ArrayList<ArrayList<Object>> getContent() {
        ArrayList<ArrayList<Object>> content = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(fileName));
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                ArrayList<Object> r = new ArrayList<>(Arrays.asList(data));
                content.add(r);
            }
            csvReader.close();
        } catch (Exception e) {
            System.out.println("FILE NOT FOUND");
        }
        ArrayList<Object> row;
        int max = 0;
        for (int y = 0; y < content.size(); y++) {
            row = content.get(y);
            max = Math.max(max,content.get(y).size());
            for (int x = 0; x < row.size(); x++) {
                if (content.get(y).get(x).toString().length() == 0) {
                    ArrayList<Object> temp = content.get(y);
                    temp.set(x,null);
                    content.set(y,temp);
                }
            }
        }
        for (int y = 0; y < content.size(); y++) {
            row = content.get(y);
            for (int x = 0; x < row.size(); x++) {
                while (row.size() < max) {
                    row.add(null);
                }
                content.set(y,row);
            }
        }
        return content;
    }
}
