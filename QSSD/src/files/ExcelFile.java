package files;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import parse.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelFile extends TabSeperatedFile{
    private List<XSSFSheet> sheets;

    public ExcelFile(String f) {
        File myFile = new File(f);
        try {
            FileInputStream fis = new FileInputStream(myFile);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            this.sheets = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                this.sheets.add(wb.getSheetAt(i));
            }
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND");
        } catch (IOException e) {
            System.out.println("NOT AN EXCEL FILE");
        }
    }

    public ExcelFile(ParseTable pT) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet();
        String[] headers = pT.getHeaderNames();
        Row r = s.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(headers[i]);
        }
        for (int i = 0; i < pT.rowCount(); i++) {
            r = s.createRow(i+1);
            ArrayList<Object> pTo = pT.getRow(i);
            for (int j = 0; j < pTo.size(); j++) {
                Cell c = r.createCell(j);
                if (pTo.get(j) instanceof String) {
                    c.setCellValue((String) pTo.get(j));
                } else if (pTo.get(j) instanceof Integer) {
                    c.setCellValue((Integer) pTo.get(j));
                } else if (pTo.get(j) instanceof Double) {
                    c.setCellValue((Double) pTo.get(j));
                }
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream("output.xlsx");
            wb.write(outputStream);
            wb.close();
            System.out.println("DONE");
        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println(e);
        }
    }

    private ArrayList<ArrayList<Object>> readFile(int sheetIndex) {
        ArrayList<ArrayList<Object>> content = new ArrayList<>();
        for (Row r : this.sheets.get(sheetIndex)) {
            Iterator<Cell> cI = r.iterator();
            ArrayList<Object> row = new ArrayList<>();
            while (cI.hasNext()) {
                Cell c = cI.next();
                if (c.getCellType() == CellType.STRING) {
                    row.add(c.getStringCellValue());
                } else if (c.getCellType() == CellType.NUMERIC) {
                    double d = c.getNumericCellValue();
                    if (HSSFDateUtil.isCellDateFormatted(c)) {
                        row.add(c.getDateCellValue());
                    } else if (d % 1 == 0) {
                        row.add((int) d);
                    } else {
                        row.add(d);
                    }
                } else if (c.getCellType() == CellType.BOOLEAN) {
                    row.add(c.getBooleanCellValue());
                }
            }
            content.add(row);
        }
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i).size() < 1) {
                content.remove(i);
                i = 0;
            }
        }
        return content;
    }

    @Override
    public ArrayList<ParseTable> getTables() {
        ArrayList<ParseTable> content = new ArrayList<>();
        for (int i = 0; i < this.sheets.size(); i++) {
            content.add(new ParseTable(this.readFile(i)));
        }
        return content;
    }
}
