import parse.*;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        excelFile ef = new excelFile("./testing/perfect.xlsx");
        ArrayList<ArrayList<Object>> fc = ef.getContent(0);
        parseTable pT = new parseTable(fc);
        System.out.println(pT);
    }
}
