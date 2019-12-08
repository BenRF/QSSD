package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;

public class TableViewWindow extends JFrame {
    ParseTable pT;
    JTable tab;
    JTableHeader tabHeader;
    JScrollPane scrollPane;

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
        scrollPane = new JScrollPane();
        scrollPane.setLayout(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0,0,decidedWidth-15,decidedHeight);
        String[] headers = pT.getHeaderNames();
        String[][] content = pT.getContent();
        tab = new JTable(content, headers);
        tabHeader = tab.getTableHeader();
        tab.setBounds(10,30,decidedWidth-40,pT.rowCount()*16);
        tabHeader.setBounds(10,10,decidedWidth-40,20);
        scrollPane.add(tabHeader);
        scrollPane.add(tab);
        tab.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (this.pT.isProblem(column,row)) {
                c.setFont(new Font("Courier", Font.BOLD, 12));
            }
            return c;
        });
        this.getContentPane().add(scrollPane);
        this.setVisible(true);
    }

    public void resize() {
        int decidedWidth = this.getWidth() - 40;
        if (decidedWidth > pT.colCount() * 150) {
            decidedWidth = pT.colCount() * 150;
        }
        tab.setBounds(10,30,decidedWidth,pT.rowCount()*16);
        tabHeader.setBounds(10,10,decidedWidth,20);
        scrollPane.setBounds(0,0,this.getWidth(),this.getHeight());
        this.revalidate();
    }
}
