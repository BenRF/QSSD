package gui;

import files.CSVFile;
import files.ExcelFile;

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
    private static ParseTable result;
    private ArrayList<ArrayList<Link>> links;
    private LinkPanel lP;
    private ArrayList<StepButton> steps;
    private JButton next,previous;

    MergingPanel() {
        this.setLayout(null);
        this.steps = new ArrayList<>();
    }

    void setup() {
        this.removeAll();
        this.revalidate();
        this.updateUI();
        this.repaint();
        this.next = new JButton(">");
        this.next.setBounds(85,260,45,25);
        this.next.addActionListener(e -> this.nextTable());
        this.previous = new JButton("<");
        this.previous.setBounds(30,260,45,25);
        this.previous.addActionListener(e -> this.prevTable());
        this.lP = new LinkPanel();
        this.add(this.lP);
        this.tabs = MainWindow.getTables();
        this.results = new ParseTable[this.tabs.size()-1];
        Arrays.fill(this.results, null);
        this.orderTables();
        System.out.println("Order: " + this.tabs.toString());
        if (this.tabs.get(0).getLinks(this.tabs.get(1)).size() == 0) {
            this.results[0] = null;
        } else {
            this.results[0] = new ParseTable(this.tabs.get(0), this.tabs.get(1));
        }
        System.out.println("Results: " + Arrays.toString(this.results));
        for (int i = 2; i < this.tabs.size(); i++) {
            if (this.results[i-2] != null && this.results[i-2].getLinks(this.tabs.get(i)).size() > 0) {
                this.results[i-1] = new ParseTable(this.results[i-2], this.tabs.get(i));
            }
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
            if (before == null) {
                this.links.add(null);
            } else {
                this.links.add(before.getLinks(with));
            }
        }
        int width = (this.getWidth()-80)/(this.tabs.size()-1);
        this.steps = new ArrayList<>();
        StepButton temp = new StepButton(this.tabs.get(0).getName() + " + " + this.tabs.get(1).getName(),0,this,width);
        this.steps.add(temp);
        for (int i = 2; i < this.tabs.size(); i++) {
            if (this.results[i-2] != null) {
                temp = new StepButton("+ " + this.tabs.get(i).getName(), i - 1, this, width);
                this.steps.add(temp);
            }
        }
        this.steps.get(this.steps.size()-1).setEnabled(false);
        this.step = this.tabs.size()-1;
        while (this.results[this.step-1] == null) {
            this.step--;
        }
        this.step++;
        this.update();
    }

    private int unlinkedTableCount() {
        int pos = this.results.length-1;
        while (this.results[pos] == null) {
            pos--;
        }
        return this.results.length - 1 - pos;
    }

    private void nextTable() {
        int pos = this.tabs.size() - this.unlinkedTableCount();
        this.tabs.add(this.tabs.get(pos));
        this.tabs.remove(pos);
        this.update();
    }

    private void prevTable() {
        int pos = this.tabs.size() - this.unlinkedTableCount();
        this.tabs.add(pos,this.tabs.get(this.tabs.size()-1));
        this.tabs.remove(this.tabs.size()-1);
        this.update();
    }

    public void reCalcResultsFromCurrent() {
        for (int i = this.step; i < this.tabs.size(); i++) {
            if (i - 2 >= 0) {
                if (this.links.get(i-1) != null) {
                    this.results[i-1] = new ParseTable(this.results[i - 2], this.tabs.get(i), this.links.get(i - 1));
                } else {
                    this.results[i-1] = null;
                }
            } else {
                if (this.links.get(0) == null) {
                    this.results[i-1] = new ParseTable(this.tabs.get(0),this.tabs.get(i));
                } else {
                    this.results[i-1] = new ParseTable(this.tabs.get(0), this.tabs.get(i), this.links.get(0));
                }
            }
        }
    }

    public void updateLinksAtCurrentStep() {
        ArrayList<Link> links = this.lP.getLinks();
        this.links.set(this.step - 1, links);
        if (links == null) {
            this.reCalcResultsFromCurrent();
            this.update();
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
                } else {
                    tabs.addAll(before);
                    break;
                }
            }
            this.tabs = tabs;
        } else {
            this.tabs = before;
        }
    }

    private void update() {
        this.revalidate();
        this.repaint();
        this.removeAll();
        this.add(this.lP);
        for (StepButton b: this.steps) {
            this.add(b.getButt());
        }
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
        int decidedWidth = this.getWidth() - 80;
        for (int i = 0; i < tables.length; i++) {
            tab = tables[i];
            if (tab != null) {
                JTable jT = tab.getSummaryJTable();
                JTableHeader header = tab.getJTableHeader(jT);
                final boolean[] dragComplete = {false};
                int finalI = i;
                header.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (dragComplete[0]) {
                            tables[finalI].orderCols(jT.getColumnModel().getColumns());
                            updateLinksAtCurrentStep();
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

                    public void columnAdded(TableColumnModelEvent e) {
                    }

                    public void columnRemoved(TableColumnModelEvent e) {
                    }

                    public void columnMarginChanged(ChangeEvent e) {
                    }

                    public void columnSelectionChanged(ListSelectionEvent e) {
                    }
                });
                if (this.getWidth() - 30 > tab.getColumnCount() * 155) {
                    decidedWidth = tab.getColumnCount() * 150;
                }
                header.setBounds(30, positions[i], decidedWidth, 20);
                jT.setBounds(30, positions[i] + 20, decidedWidth, 33);
                this.add(header);
                this.add(jT);
            } else {
                this.add(this.next);
                this.add(this.previous);
            }
        }
        int y = 510;
        decidedWidth = this.getWidth() - 80;
        if (tables[2] != null) {
            for (Problem p : tables[2].getProblems()) {
                JLabel e = new JLabel();
                e.setText(p.getTitle());
                e.setBounds(30, y, decidedWidth, 15);
                this.add(e);
                y = y + 20;
            }
        }
        y = 550;
        JLabel name = new JLabel("File name:");
        name.setBounds(20,y,100,30);
        name.setFont(name.getFont().deriveFont(15.0f));
        JTextField nameInput = new JTextField();
        nameInput.setFont(nameInput.getFont().deriveFont(15.0f));
        nameInput.setBounds(115,y,200,30);
        String[] options = {".xlsx",".csv"};
        JComboBox<String> extension = new JComboBox<>(options);
        extension.setFont(extension.getFont().deriveFont(15.0f));
        extension.setBounds(325,y,100,30);
        JButton merge = new JButton("Save");
        merge.setBounds(150,y+40,100,30);
        merge.addActionListener(e -> {
            switch (extension.getSelectedIndex()) {
                case (0):
                    new ExcelFile(tables[2],nameInput.getText());
                    break;
                case (1):
                    new CSVFile(tables[2],nameInput.getText());
                    break;
            }
        });
        this.add(merge);
        this.add(extension);
        this.add(name);
        this.add(nameInput);
    }

    public void paint (Graphics g) {
        super.paint(g);
        if (this.lP.hasAltered()) {
            this.updateLinksAtCurrentStep();
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
        this.lP.paint(g,before,mergingWith,this.links.get(this.step-1));
    }

    public static ParseTable getResult() {
        return result;
    }

    public void goToStep(int i) {
        this.steps.get(this.step-1).setEnabled(true);
        this.updateLinksAtCurrentStep();
        this.step = i+1;
        this.steps.get(i).setEnabled(false);
        this.update();
    }
}
