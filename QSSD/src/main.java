import files.excelFile;
import gui.*;
import parse.*;

public class main {
    public static void main(String[] args) {
        //mainWindow main = new mainWindow();
        parseColumn p1 = new parseColumn("Id");
        parseColumn p2 = new parseColumn("Id");
        Object[] id1 = {0,1,2,3,"hi"};
        for (Object i: id1) {
            p1.addContent(i);
        }
        Object[] id2 = {0,1,2,3,3,"hi"};
        for (Object i: id2) {
            p2.addContent(i);
        }
        System.out.println("Attributes: " + p1.checkAtt(p2));
        System.out.println("Content: " + p1.checkContent(p2));
    }
}
