import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class excelFile {
    private List<XSSFSheet> sheets;

    excelFile(String f) {
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

    ArrayList<ArrayList<Object>> getContent(int sheetIndex) {
        ArrayList<ArrayList<Object>> content = new ArrayList<>();
        Iterator<Row> rI = this.sheets.get(sheetIndex).iterator();
        while (rI.hasNext()) {
            Row r = rI.next();
            Iterator<Cell> cI = r.iterator();
            ArrayList<Object> row = new ArrayList<>();
            while (cI.hasNext()) {
                Cell c = cI.next();
                if (c.getCellType() == CellType.STRING) {
                    row.add(c.getStringCellValue());
                } else if (c.getCellType() == CellType.NUMERIC) {
                    double d = c.getNumericCellValue();
                    if (d % 1 == 0) {
                        row.add( (int) d);
                    } else {
                        row.add(d);
                    }
                } else if (c.getCellType() == CellType.BOOLEAN) {
                    row.add(c.getBooleanCellValue());
                } else if (c.getCellType() == CellType.BLANK) {
                    row.add(null);
                }
            }
            content.add(row);
        }
        return content;
    }
}
