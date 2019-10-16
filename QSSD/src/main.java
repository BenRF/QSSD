import parse.*;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        excelFile ef = new excelFile("./testing/perfect.xlsx");
        parseTable pT = ef.getTables().get(0);
        System.out.println(pT);
    }
}
