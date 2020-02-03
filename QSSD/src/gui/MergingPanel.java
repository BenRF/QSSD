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
        back = new JButton("Back");
        back.setBounds(20,20,100,30);
        back.addActionListener(e -> stepBack());
        forward = new JButton("Forward");
        forward.setEnabled(false);
        forward.setBounds(150,20,100,30);
        forward.addActionListener(e -> stepForward());
        this.add(back);
        this.add(forward);
        this.tabs = MainWindow.getTables();
        this.step = this.tabs.size()-1;
        orderTables();
        update();
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
        this.add(back);
        this.add(forward);
        ParseTable pT = this.tabs.get(0);
        for (int i = 1; i <= this.step; i++) {
            pT = new ParseTable(pT,this.tabs.get(i));
        }
        JTable jT = new JTable(pT);
        JTableHeader header = jT.getTableHeader();
        int decidedWidth = this.getWidth()-80;
        if (this.getWidth() - 30 > pT.getColumnCount() * 155) {
            decidedWidth = pT.getColumnCount() * 150;
        }
        header.setBounds(30, 70, decidedWidth, 20);
        jT.setBounds(30, 90, decidedWidth, 16 * pT.getRowCount());
        this.add(header);
        this.add(jT);
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
        if (this.step <= 0) {
            this.back.setEnabled(false);
        }
        this.update();
    }
}
