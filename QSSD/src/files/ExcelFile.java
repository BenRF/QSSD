package files;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import parse.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcelFile extends TabSeperatedFile{
    private List<XSSFSheet> sheets;

    public ExcelFile(String f) {
        File myFile = new File(f);
        try {
            FileInputStream fis = new FileInputStream(myFile);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            new XSSFFormulaEvaluator(wb);
            XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
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

    public ArrayList<ArrayList<Object>> getContent() {
        ArrayList<ArrayList<Cell>> content = new ArrayList<>();
        ArrayList<ArrayList<Cell>> temp;
        int x,y;
        for (XSSFSheet sheet: this.sheets) {
            temp = new ArrayList<>();
            x = 0;
            y = 0;
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellType() != CellType.BLANK) {
                        CellAddress address = c.getAddress();
                        while (x <= address.getRow()) {
                            ArrayList<Cell> newRow = new ArrayList<>();
                            if (temp.size() > 0) {
                                for (int i = 0; i < temp.get(0).size(); i++) {
                                    newRow.add(null);
                                }
                            }
                            temp.add(newRow);
                            x++;
                        }
                        while (y <= address.getColumn()) {
                            for (ArrayList<Cell> rows : temp) {
                                rows.add(null);
                            }
                            y++;
                        }
                        x = Math.max(x, address.getRow());
                        y = Math.max(y, address.getColumn());
                        ArrayList<Cell> row = temp.get(address.getRow());
                        row.set(address.getColumn(), c);
                        temp.set(address.getRow(), row);
                    }
                }
            }
            content.addAll(temp);
            content.add(new ArrayList<>());
        }
        if (this.sheets.size() > 1) {
            int max = 0;
            for (ArrayList<Cell> row: content) {
                if (row.size() > max) {
                    max = row.size();
                }
            }
            for (int i = 0; i < content.size(); i++) {
                ArrayList<Cell> row = content.get(i);
                if (row.size() < max) {
                    while (row.size() < max) {
                        row.add(null);
                    }
                    content.set(i, row);
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
        ArrayList<ArrayList<Object>> rawResults = new ArrayList<>();
        for (ArrayList<Cell> row: content) {
            ArrayList<Object> r = new ArrayList<>();
            for (Cell c: row) {
                Object cellVal = null;
                if (c != null) {
                    if (c.getCellType() == CellType.STRING) {
                        cellVal = c.getStringCellValue();
                    } else if (c.getCellType() == CellType.FORMULA) {
                        if (c.getCachedFormulaResultType() == CellType.STRING) {
                            cellVal = c.getStringCellValue();
                        } else {
                            double d = c.getNumericCellValue();
                            if (d % 1 == 0) {
                                cellVal = (int) d;
                            } else {
                                cellVal = d;
                            }
                        }
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
                }
                r.add(cellVal);

            }
            rawResults.add(r);
        }
        return rawResults;
    }
}
