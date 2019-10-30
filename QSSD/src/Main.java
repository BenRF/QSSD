import files.ExcelFile;
import gui.*;
import parse.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //MainWindow main = new MainWindow();
        Expression e = new Expression("benramsayfoster@gmail.com");
        System.out.println(e);
        Expression e2 = new Expression("pencil5099@hotmail.co.uk");
        System.out.println(e2);
        e.compare(e2);
    }
}
