package files;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import parse.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class excelFile {
    private List<XSSFSheet> sheets;

    public excelFile(String f) {
        File myFile = new File(f);
        try {
            FileInputStream fis = new FileInputStream(myFile);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            this.sheets = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                this.sheets.add(wb.getSheetAt(i));
            }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND");
        } catch (IOException e) {
            System.out.println("NOT AN EXCEL FILE");
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
                    if (d % 1 == 0) {
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
        return content;
    }

    public ArrayList<parseTable> getTables() {
        ArrayList<parseTable> content = new ArrayList<>();
        for (int i = 0; i < this.sheets.size(); i++) {
            content.add(new parseTable(this.readFile(i)));
        }
        return content;
    }
}
