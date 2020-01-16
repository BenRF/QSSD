package files;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import parse.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcelFile implements TabSeperatedFile{
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

    public ExcelFile(ParseTable pT,String name) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet();
        String[] headers = pT.getHeaderNames();
        Row r = s.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(headers[i]);
        }
        for (int i = 0; i < pT.getRowCount(); i++) {
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
            FileOutputStream outputStream = new FileOutputStream(name + ".xlsx");
            wb.write(outputStream);
            wb.close();
            outputStream.flush();
            outputStream.close();
            System.out.println("DONE");
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public ArrayList<ArrayList<Object>> readFile(int sheetIndex) {
        ArrayList<ArrayList<Cell>> content = new ArrayList<>();
        int x = 0;
        int y = 0;
        for (Row r : this.sheets.get(sheetIndex)) {
            for (Cell c : r) {
                if (c.getCellType() != CellType.BLANK) {
                    CellAddress address = c.getAddress();
                    while (x <= address.getRow()) {
                        ArrayList<Cell> newRow = new ArrayList<>();
                        if (content.size() > 0) {
                            for (int i = 0; i < content.get(0).size(); i++) {
                                newRow.add(null);
                            }
                        }
                        content.add(newRow);
                        x++;
                    }
                    while (y <= address.getColumn()) {
                        for (ArrayList<Cell> rows : content) {
                            rows.add(null);
                        }
                        y++;
                    }
                    x = Math.max(x, address.getRow());
                    y = Math.max(y, address.getColumn());
                    ArrayList<Cell> row = content.get(address.getRow());
                    row.set(address.getColumn(), c);
                    content.set(address.getRow(), row);
                }
            }
        }
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i).size() < 1) {
                content.remove(i);
                i = 0;
            }
        }
        boolean emptyRow = true;
        boolean removed = false;
        int i = 0;
        while (i < content.size()) {
            ArrayList<Cell> row = content.get(i);
            if (Collections.frequency(row,null) == row.size()) {
                content.set(i,new ArrayList<>());
                if (emptyRow) {
                    content.remove(i);
                    removed = true;
                }
                emptyRow = true;
            } else {
                emptyRow = false;
            }
            if (removed) {
                i = 0;
                removed = false;
            } else {
                i++;
            }
        }
        int start,finish;
        for (int z = 0; z < content.size(); z++) {
            ArrayList<Cell> row = content.get(z);
            if (row.size() > 0) {
                start = 0;
                finish = row.size() - 1;
                while (row.get(start) == null) {
                    start++;
                }
                while (row.get(finish) == null) {
                    finish--;
                }
                ArrayList<Cell> subset = new ArrayList<>(row.subList(start,finish+1));
                content.set(z,subset);
            }
        }
        ArrayList<ArrayList<Object>> results = new ArrayList<>();
        for (ArrayList<Cell> row: content) {
            ArrayList<Object> r = new ArrayList<>();
            for (Cell c: row) {
                Object cellVal = null;
                if (c.getCellType() == CellType.STRING) {
                    cellVal = c.getStringCellValue();
                } else if (c.getCellType() == CellType.NUMERIC) {
                    double d = c.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(c)) {
                        cellVal = c.getDateCellValue();
                    } else if (d % 1 == 0) {
                        cellVal = (int) d;
                    } else {
                        cellVal = d;
                    }
                } else if (c.getCellType() == CellType.BOOLEAN) {
                    cellVal = c.getBooleanCellValue();
                }
                r.add(cellVal);
            }
            results.add(r);
        }
        return results;
    }

    @Override
    public ArrayList<ParseTable> getTables() {
        ArrayList<ParseTable> tabs = new ArrayList<>();
        for (int i = 0; i < this.sheets.size(); i++) {
            ArrayList<ArrayList<Object>> sheet = this.readFile(i);
            ArrayList<ArrayList<Object>> tableContent = new ArrayList<>();
            for (ArrayList<Object> row: sheet) {
                if (row.size() > 0) {
                    tableContent.add(row);
                } else {
                    tabs.add(new ParseTable(tableContent));
                    tableContent = new ArrayList<>();
                }
            }
            tabs.add(new ParseTable(tableContent));
        }
        return tabs;
    }
}
