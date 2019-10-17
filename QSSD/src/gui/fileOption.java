package gui;
import parse.*;
import files.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class fileOption extends JPanel {
    private String name;
    private String fileLocation;
    private ArrayList<parseTable> tables;
    private excelFile f;

    public fileOption(String fileLocation) {
        this.f = new excelFile(fileLocation);
        this.fileLocation = fileLocation;
        this.name = "";
        for (int i = fileLocation.length()-1; i > 0; i--) {
            char c = fileLocation.charAt(i);
            if (c != '\\') {
                this.name = c + this.name;
            } else {
                break;
            }
        }
        this.tables = this.f.getTables();
        this.setSize(new Dimension(430,100));
        this.setOpaque(false);

        JLabel n = new JLabel();
        n.setText(this.name);
        n.setBounds(15,10,400,30);
        this.add(n);

        for (int i = 0; i < this.tables.size(); i++) {
            parseTable pt = this.tables.get(i);
            String[] headers = pt.getHeaderNames();
            String[][] content = pt.getColumnAttributes();
            JTable jT = new JTable(content,headers);
            JTableHeader header = jT.getTableHeader();
            header.setBounds(15,45,400,20);
            jT.setBounds(15,65,400,30);
            this.add(header);
            this.add(jT);
        }

        this.setBackground(Color.white);
        this.setLayout(null);
        this.setVisible(true);
    }
}
