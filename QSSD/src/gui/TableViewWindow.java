package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class TableViewWindow extends JFrame {
    public TableViewWindow(ParseTable pT) {
        this.setTitle("TABLE VIEW");
        this.setBounds(50,50,400,40 + (pT.rowCount() * 23));
        String[] headers = pT.getHeaderNames();
        String[][] content = pT.getContent();
        JTable jT = new JTable(content, headers);
        JTableHeader header = jT.getTableHeader();
        int decidedWidth = this.getWidth()-80;
        if (this.getWidth()-30 > headers.length * 155) {
            decidedWidth = headers.length * 150;
        }
        header.setBounds(30, 30, decidedWidth, 20);
        jT.setBounds(30, 50, decidedWidth, 16 * content.length);
        this.setLayout(null);
        this.add(header);
        this.add(jT);
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }
}
