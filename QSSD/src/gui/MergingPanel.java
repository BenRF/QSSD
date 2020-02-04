package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.util.ArrayList;

class MergingPanel extends JPanel {
    private int step;
    private ArrayList<ParseTable> tabs;
    private JButton forward,back;

    MergingPanel() {
        this.setLayout(null);
    }

    void setup() {
        this.removeAll();
        this.revalidate();
        this.updateUI();
        this.repaint();
        this.back = new JButton("Back");
        this.back.setBounds(20,20,100,30);
        this.back.addActionListener(e -> stepBack());
        this.forward = new JButton("Forward");
        this.forward.setBounds(150,20,100,30);
        this.forward.addActionListener(e -> stepForward());
        this.forward.setEnabled(false);
        this.add(this.back);
        this.add(this.forward);
        this.tabs = MainWindow.getTables();
        this.step = this.tabs.size()-1;
        if (this.step == 1) {
            this.back.setEnabled(false);
        }
        this.orderTables();
        this.update();
    }

    private void orderTables() {
        ArrayList<ParseTable> before = new ArrayList<>(this.tabs);
        ArrayList<ParseTable> tabs = new ArrayList<>();
        int[][] scores = new int[before.size()][before.size()];
        for (int i = 0; i < before.size(); i++) {
            for (int j = 0; j < before.size(); j++) {
                int s = -1;
                if (i != j) {
                    s = before.get(i).getLinks(before.get(j)).size();
                }
                scores[i][j] = s;
            }
        }
        int max = -2, x = -1, y = -1;
        for (int i = 0; i < scores.length; i++) {
            int[] row = scores[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] > max && row[j] > 0) {
                    max = row[j];
                    y = i;
                    x = j;
                }
            }
        }
        if (max > 0) {
            tabs.add(before.get(x));
            before.remove(x);
            tabs.add(before.get(y));
            if (x > y) {
                before.remove(y);
            } else {
                before.remove(y-1);
            }
        }
        while (before.size() > 0) {
            ParseTable pT = new ParseTable(tabs.get(0),tabs.get(1));
            for (int i = 2; i < tabs.size(); i++) {
                pT = new ParseTable(pT,tabs.get(i));
            }
            int[] results = new int[before.size()];
            for (int i = 0; i < results.length; i++) {
                results[i] = pT.getLinks(before.get(i)).size();
            }
            max = -2;
            x = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    max = results[i];
                    x = i;
                }
            }
            if (max > 0) {
                tabs.add(before.get(x));
                before.remove(x);
            }
        }
        this.tabs = tabs;
    }

    private void update() {
        this.removeAll();
        this.revalidate();
        this.updateUI();
        this.repaint();
        this.add(this.back);
        this.add(this.forward);
        ParseTable[] tables = new ParseTable[3];
        tables[0] = this.tabs.get(0);
        tables[2] = this.tabs.get(0);
        for (int i = 1; i <= this.step; i++) {
            tables[2] = new ParseTable(tables[2],this.tabs.get(i));
            if (i == this.step - 1) {
                tables[0] = new ParseTable(tables[2]);
            } else if (i == this.step) {
                tables[1] = this.tabs.get(i);
            }
        }
        int[] positions = new int[]{60,150,300};
        ParseTable tab;
        int decidedWidth;
        for (int i = 0; i < tables.length; i++) {
            decidedWidth = this.getWidth() - 80;
            tab = tables[i];
            JTable jT = tab.getSummaryJTable();
            JTableHeader header = tab.getJTableHeader(jT);
            if (this.getWidth() - 30 > tab.getColumnCount() * 155) {
                decidedWidth = tab.getColumnCount() * 150;
            }
            header.setBounds(30, positions[i], decidedWidth, 20);
            jT.setBounds(30, positions[i]+20, decidedWidth, 33);
            this.add(header);
            this.add(jT);
        }
    }

    private void stepForward() {
        this.step++;
        if (this.step >= 1) {
            this.back.setEnabled(true);
        }
        if (this.step == this.tabs.size()-1) {
            this.forward.setEnabled(false);
        }
        this.update();
    }

    private void stepBack() {
        this.step--;
        if (this.step < this.tabs.size()-1) {
            this.forward.setEnabled(true);
        }
        if (this.step <= 1) {
            this.back.setEnabled(false);
        }
        this.update();
    }
}
