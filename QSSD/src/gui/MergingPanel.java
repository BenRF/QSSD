package gui;

import parse.Link;
import parse.ParseTable;
import parse.problems.Problem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

class MergingPanel extends JPanel {
    private int step;
    private ArrayList<ParseTable> tabs;
    private ParseTable[] results;
    private JButton forward,back;
    private static ParseTable result;
    private ArrayList<ArrayList<Link>> links;
    private LinkPanel lP;

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
        this.lP = new LinkPanel();
        this.add(this.lP);
        this.add(this.back);
        this.add(this.forward);
        this.tabs = MainWindow.getTables();
        this.results = new ParseTable[this.tabs.size()-1];
        this.step = this.tabs.size()-1;
        if (this.step == 1) {
            this.back.setEnabled(false);
        }
        this.orderTables();
        this.results[0] = new ParseTable(this.tabs.get(0),this.tabs.get(1));
        for (int i = 2; i < this.tabs.size(); i++) {
            this.results[i-1] = new ParseTable(this.results[i-2],this.tabs.get(i));
        }
        result = this.results[this.results.length-1];
        this.links = new ArrayList<>();
        ParseTable before,with;
        for (int i = 1; i <= this.results.length; i++) {
            if (i - 2 < 0) {
                before = this.tabs.get(0);
            } else {
                before = this.results[i - 2];
            }
            with = this.tabs.get(i);
            this.links.add(before.getLinks(with));
        }
        this.update();
    }

    public void reCalcResultsFromCurrent() {
        this.updateLinksAtCurrentStep();
        for (int i = this.step; i < this.tabs.size(); i++) {
            if (i - 2 >= 0) {
                this.results[i - 1] = new ParseTable(this.results[i - 2], this.tabs.get(i), this.links.get(i-1));
            } else {
                this.results[i - 1] = new ParseTable(this.tabs.get(0), this.tabs.get(i), this.links.get(0));
            }
        }
    }

    public void updateLinksAtCurrentStep() {
        if (this.lP.hasAltered()) {
            this.links.set(this.step - 1, this.lP.getLinks());
        }
    }

    private void orderTables() {
        ArrayList<ParseTable> before = new ArrayList<>(tabs);
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
        this.revalidate();
        this.repaint();
        this.removeAll();
        this.add(this.lP);
        this.add(this.back);
        this.add(this.forward);
        ParseTable[] tables = new ParseTable[3];
        if (this.step - 2 < 0) {
            tables[0] = this.tabs.get(0);
        } else {
            tables[0] = this.results[this.step - 2];
        }
        tables[1] = this.tabs.get(step);
        tables[2] = this.results[this.step-1];
        int[] positions = new int[]{60, 200, 450};
        ParseTable tab;
        int decidedWidth;
        for (int i = 0; i < tables.length; i++) {
            decidedWidth = this.getWidth() - 80;
            tab = tables[i];
            JTable jT = tab.getSummaryJTable();
            JTableHeader header = tab.getJTableHeader(jT);
            final boolean[] dragComplete = {false};
            int finalI = i;
            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (dragComplete[0]) {
                        tables[finalI].orderCols(jT.getColumnModel().getColumns());
                        reCalcResultsFromCurrent();
                        update();
                    }
                    dragComplete[0] = false;
                }
            });
            jT.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
                public void columnMoved(TableColumnModelEvent e) {
                    dragComplete[0] = true;
                }
                public void columnAdded(TableColumnModelEvent e) { }
                public void columnRemoved(TableColumnModelEvent e) { }
                public void columnMarginChanged(ChangeEvent e) { }
                public void columnSelectionChanged(ListSelectionEvent e) { }
            });
            if (this.getWidth() - 30 > tab.getColumnCount() * 155) {
                decidedWidth = tab.getColumnCount() * 150;
            }
            header.setBounds(30, positions[i], decidedWidth, 20);
            jT.setBounds(30, positions[i]+20, decidedWidth, 33);
            this.add(header);
            this.add(jT);
        }
        int y = 510;
        decidedWidth = this.getWidth() - 80;
        for (Problem p : tables[2].getProblems()) {
            JLabel e = new JLabel();
            e.setText(p.getTitle());
            e.setBounds(30, y, decidedWidth, 15);
            this.add(e);
            y = y + 20;
        }
        //this.links = this.links.get(step);
    }

    public void paint (Graphics g) {
        super.paint(g);
        if (this.lP.hasAltered()) {
            this.reCalcResultsFromCurrent();
            this.update();
        }
        Graphics2D g2 = (Graphics2D) g;
        ParseTable before;
        if (this.step - 2 < 0) {
            before = this.tabs.get(0);
        } else {
            before = this.results[this.step - 2];
        }
        ParseTable mergingWith = this.tabs.get(this.step);
        g2.setStroke(new BasicStroke(2));
        this.lP.paint(g,before,mergingWith,this.links.get(step-1));
    }

    public static ParseTable getResult() {
        return result;
    }

    private void stepForward() {
        this.updateLinksAtCurrentStep();
        this.step++;
        if (this.step >= 1) {
            this.back.setEnabled(true);
        }
        if (this.step == tabs.size()-1) {
            this.forward.setEnabled(false);
        }
        this.update();
    }

    private void stepBack() {
        this.updateLinksAtCurrentStep();
        this.step--;
        if (this.step < tabs.size()-1) {
            this.forward.setEnabled(true);
        }
        if (this.step <= 1) {
            this.back.setEnabled(false);
        }
        this.update();
    }
}
