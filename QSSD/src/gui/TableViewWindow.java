package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TableViewWindow extends JFrame {
    ParseTable pT;
    JTable tab;
    JTableHeader tabHeader;
    public TableViewWindow(ParseTable pT) {
        this.pT = pT;
        this.setTitle("TABLE VIEW");
        int decidedWidth = 40 + (pT.colCount() * 150);
        if (decidedWidth > 1600) {
            decidedWidth = 1600;
        }
        int decidedHeight = 80 + (pT.rowCount() * 16);
        this.setBounds(50,50,decidedWidth,decidedHeight);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resize();
            }
        });
        String[] headers = pT.getHeaderNames();
        String[][] content = pT.getContent();
        tab = new JTable(content, headers);
        tabHeader = tab.getTableHeader();
        tab.setBounds(10,30,decidedWidth-40,pT.rowCount()*16);
        tabHeader.setBounds(10,10,decidedWidth-40,20);
        this.add(tabHeader);
        this.add(tab);
        this.setVisible(true);
    }

    public void resize() {
        int decidedWidth = this.getWidth() - 40;
        if (decidedWidth > pT.colCount() * 150) {
            decidedWidth = pT.colCount() * 150;
        }
        tab.setBounds(10,30,decidedWidth,pT.rowCount()*16);
        tabHeader.setBounds(10,10,decidedWidth,20);
        this.revalidate();
    }
}
