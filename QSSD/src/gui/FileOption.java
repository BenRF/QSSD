package gui;
import parse.*;
import files.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

class FileOption extends JPanel {
    private String name;
    private ArrayList<ParseTable> tables;

    FileOption(ParseTable pT) {
        this.tables = new ArrayList<>();
        this.tables.add(pT);
        this.name = "OUTPUT";
        this.draw();
    }

    FileOption(String fileLocation) {
        TabSeperatedFile f = null;
        if (fileLocation.substring(fileLocation.length()-5).equals(".xlsx")) {
            f = new ExcelFile(fileLocation);
        } else if (fileLocation.substring(fileLocation.length()-4).equals(".csv")) {
            f = new CSVFile(fileLocation);
        }
        System.out.println(fileLocation.substring(fileLocation.length()-4));
        if (f != null) {
            String name = "";
            for (int i = fileLocation.length() - 1; i > 0; i--) {
                char c = fileLocation.charAt(i);
                if (c != '\\') {
                    name = c + name;
                } else {
                    break;
                }
            }
            this.name = name;
            this.tables = f.getTables();
            //this.setSize(new Dimension(430,50 + (this.tables.size()*100)));
            this.setOpaque(false);
            this.draw();
        } else {
            System.out.println("ERROR WITH CREATING FILE OBJECT");
        }
    }

    private void draw() {
        JLabel n = new JLabel();
        n.setText(this.name);
        n.setBounds(15,10,400,30);
        this.add(n);
        for (int i = 0; i < this.tables.size(); i++) {
            ParseTable pt = this.tables.get(i);
            String[] headers = pt.getHeaderNames();
            String[][] content = pt.getColumnAttributes();
            JTable jT = new JTable(content, headers);
            JTableHeader header = jT.getTableHeader();
            header.setBounds(15, 45+(i*50), 380, 20);
            jT.setBounds(15, 65+(i*50), 380, 30);
            this.add(header);
            this.add(jT);
        }
        //this.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.RED));
        this.setPreferredSize(new Dimension(400,20 + (this.tables.size()*70)));
        this.setBackground(Color.white);
        this.setLayout(null);
        this.setVisible(true);
    }

    String getFileName() {
        return this.name;
    }

    ArrayList<ParseTable> getTables() {
        return this.tables;
    }

    ParseTable getTable(int i) {
        return this.tables.get(i);
    }
}
