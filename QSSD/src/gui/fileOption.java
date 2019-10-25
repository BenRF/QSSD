package gui;
import parse.*;
import files.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

class fileOption extends JPanel {
    private String name;
    private ArrayList<parseTable> tables;

    fileOption(parseTable pT) {
        this.tables = new ArrayList<>();
        this.tables.add(pT);
        this.name = "OUTPUT";
        this.draw();
    }

    fileOption(String fileLocation) {
        excelFile f = new excelFile(fileLocation);
        String name = "";
        for (int i = fileLocation.length()-1; i > 0; i--) {
            char c = fileLocation.charAt(i);
            if (c != '\\') {
                name = c + name;
            } else {
                break;
            }
        }
        this.name = name;
        this.tables = f.getTables();
        this.setSize(new Dimension(430,50));
        this.setOpaque(false);
        this.draw();
    }

    private void draw() {
        JLabel n = new JLabel();
        n.setText(this.name);
        n.setBounds(15,10,400,30);
        this.add(n);
        for (parseTable pt : this.tables) {
            String[] headers = pt.getHeaderNames();
            String[][] content = pt.getColumnAttributes();
            JTable jT = new JTable(content, headers);
            JTableHeader header = jT.getTableHeader();
            header.setBounds(15, 45, 380, 20);
            jT.setBounds(15, 65, 380, 30);
            this.add(header);
            this.add(jT);
        }
        this.setPreferredSize(new Dimension(400,100));
        this.setBackground(Color.white);
        this.setLayout(null);
        this.setVisible(true);
    }

    ArrayList<parseTable> getTables() {
        return this.tables;
    }
}
