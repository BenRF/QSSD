import files.ExcelFile;
import gui.MainWindow;
import parse.ParseTable;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        new MainWindow();
        ExcelFile f = new ExcelFile("E:\\Uni work\\ce301_ramsay_foster_b\\QSSD\\testing\\UniUseCase.xlsx");
        for (ParseTable tab: f.getTables()) {
            tab.toConsole();
            System.out.println("");
        }
    }
}
