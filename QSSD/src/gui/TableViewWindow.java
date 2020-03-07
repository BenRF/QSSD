package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableViewWindow extends JFrame {
    private ParseTable pT;
    private JTable tab;
    private JTableHeader tabHeader;
    private JCheckBox viewProblems;
    private JPanel innerScroll;
    private JScrollPane scrollable;

    public TableViewWindow(ParseTable pT) {
        this.pT = pT;
        this.setTitle("TABLE VIEW");
        int decidedWidth = 40 + (pT.getColumnCount() * 150);
        if (decidedWidth > 1600) {
            decidedWidth = 1600;
        }
        int decidedHeight = 150 + (pT.getRowCount() * 16);
        if (decidedHeight > 800) {
            decidedHeight = 800;
        }
        this.setBounds(MainWindow.getProgramXPos() + (MainWindow.getWidth()/2) - (decidedWidth/2), MainWindow.getProgramYPos() + (MainWindow.getHeight()/2) - (decidedHeight/2), decidedWidth,decidedHeight);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resize();
            }
        });
        this.viewProblems = new JCheckBox();
        this.viewProblems.setSelected(false);
        this.viewProblems.setEnabled(pT.hasProblems());
        this.viewProblems.addItemListener(e -> this.viewProblems());
        this.viewProblems.setBounds(10, 5, 20, 20);
        this.add(viewProblems);
        JLabel problemView = new JLabel();
        problemView.setText("Problems only");
        problemView.setBounds(35,5,100,20);
        this.add(problemView);
        this.innerScroll = new JPanel();
        this.innerScroll.setPreferredSize(new Dimension(decidedWidth-40,75 + pT.getRowCount()*16));
        this.innerScroll.setVisible(true);
        this.innerScroll.setLayout(null);
        this.scrollable = new JScrollPane(innerScroll);
        this.scrollable.setBounds(0,40,decidedWidth-20,decidedHeight-90);
        this.scrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.getContentPane().add(scrollable);
        this.tab = pT.getFullJTable();
        this.tabHeader = tab.getTableHeader();
        this.tabHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tab.columnAtPoint(e.getPoint());
                String name = tab.getColumnName(col);
                pT.sortByColName(name);
                tab.setModel(new DefaultTableModel(pT.getContent(),pT.getHeaderNames()));
            }
        });
        final boolean[] dragComplete = {false};
        this.tabHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragComplete[0]) {
                    pT.orderCols(tab.getColumnModel().getColumns());
                }
                dragComplete[0] = false;
            }
        });
        this.tab.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            public void columnMoved(TableColumnModelEvent e) {
                dragComplete[0] = true;
            }
            public void columnAdded(TableColumnModelEvent e) { }
            public void columnRemoved(TableColumnModelEvent e) { }
            public void columnMarginChanged(ChangeEvent e) { }
            public void columnSelectionChanged(ListSelectionEvent e) { }
        });
        this.tabHeader.setBounds(10,0, decidedWidth-150,20);
        this.tab.setBounds(10,20, decidedWidth-150,pT.getRowCount()*16);
        this.innerScroll.add(tabHeader);
        this.innerScroll.add(tab);
        this.tab.setDefaultRenderer(Object.class, (table, value, isSelected, hasFocus, row, column) -> {
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
            DefaultTableModel dTM = new DefaultTableModel(this.pT.getProblemContent(),this.pT.getColumnNames());
            this.tab.setModel(dTM);
        } else {
            this.tab.setModel(this.pT);
        }
    }

    private void resize() {
        int decidedWidth = this.getWidth() - 40;
        if (decidedWidth > this.pT.getColumnCount() * 150) {
            decidedWidth = this.pT.getColumnCount() * 150;
        }
        int decidedHeight = 100 + (pT.getRowCount() * 16);
        if (decidedHeight > this.getHeight()) {
            decidedHeight = this.getHeight();
            this.innerScroll.setPreferredSize(new Dimension(decidedWidth-40,20+pT.getRowCount()*16));
            this.scrollable.setBounds(10,40,decidedWidth-20,decidedHeight-90);
        } else {
            this.innerScroll.setPreferredSize(new Dimension(decidedWidth-35,20+pT.getRowCount()*16));
            this.scrollable.setBounds(10,40,decidedWidth-30,23+pT.getRowCount()*16);
        }
        this.tabHeader.setBounds(0,0,decidedWidth-35,20);
        this.tab.setBounds(0,20,decidedWidth-35,pT.getRowCount()*16);
        this.revalidate();
    }
}
