package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TableViewWindow extends JFrame {
    private ParseTable pT;
    private JTable tab;
    private JTableHeader tabHeader;
    private JCheckBox viewProblems;

    public TableViewWindow(ParseTable pT) {
        this.pT = pT;
        this.setTitle("TABLE VIEW");
        int decidedWidth = 40 + (pT.getColumnCount() * 150);
        if (decidedWidth > 1600) {
            decidedWidth = 1600;
        }
        int decidedHeight = 100 + (pT.getRowCount() * 16);
        this.setBounds(50,50, decidedWidth,decidedHeight);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resize();
            }
        });
        viewProblems = new JCheckBox();
        viewProblems.setSelected(false);
        viewProblems.addItemListener(e -> this.viewProblems());
        viewProblems.setBounds(10, 5, 20, 20);
        this.add(viewProblems);
        JLabel problemView = new JLabel();
        problemView.setText("Problems only");
        problemView.setBounds(35,5,100,20);
        this.add(problemView);
        tab = new JTable(pT);
        tabHeader = tab.getTableHeader();
        tabHeader.setBounds(10,30, decidedWidth -40,20);
        tab.setBounds(10,50, decidedWidth -40,pT.getRowCount()*16);
        this.add(tabHeader);
        this.add(tab);


        tab.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, column) -> {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (this.pT.isProblem(column,row)) {
                c.setFont(new Font("Courier", Font.BOLD, 12));
            }
            return c;
        });
        this.setVisible(true);
    }

    private void viewProblems() {
        if (this.viewProblems.isSelected()) {
            DefaultTableModel dTM = new DefaultTableModel(pT.getProblemContent(),pT.getColumnNames());
            tab.setModel(dTM);
        } else {
            tab.setModel(pT);
        }
    }

    private void resize() {
        int decidedWidth = this.getWidth() - 40;
        if (decidedWidth > pT.getColumnCount() * 150) {
            decidedWidth = pT.getColumnCount() * 150;
        }
        tabHeader.setBounds(10,30,decidedWidth,20);
        tab.setBounds(10,50,decidedWidth,pT.getRowCount()*16);
        this.revalidate();
    }
}
