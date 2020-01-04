package gui;
import parse.*;
import files.*;
import parse.problems.Problem;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class FileOption extends JPanel {
    private String name;
    private ArrayList<ParseTable> tables;
    private List<Boolean> active;
    private int Width,height;
    private int id;
    private ImportingPanel pane;
    private ArrayList<JTable> tab;
    private ArrayList<JTableHeader> Tabheaders;

    FileOption(String fileLocation,ImportingPanel pane,int id) {
        tab = new ArrayList<>();
        Tabheaders = new ArrayList<>();
        this.id = id;
        this.pane = pane;
        this.Width = this.pane.getWidth();
        TabSeperatedFile f = null;
        if (fileLocation.substring(fileLocation.length()-5).equals(".xlsx")) {
            f = new ExcelFile(fileLocation);
        } else if (fileLocation.substring(fileLocation.length()-4).equals(".csv")) {
            f = new CSVFile(fileLocation);
        }
        String name = "";
        for (int i = fileLocation.length() - 1; i > 0; i--) {
            char c = fileLocation.charAt(i);
            if (c != '\\') {
                name = c + name;
            } else {
                break;
            }
        }
        this.name = name;
        if (f != null) {
            this.tables = f.getTables();
            this.active = new ArrayList<>(Arrays.asList(new Boolean[this.tables.size()]));
            Collections.fill(this.active, Boolean.TRUE);
            this.setOpaque(false);
            this.draw();
        } else {
            this.active = new ArrayList<>();
            this.tables = new ArrayList<>();
            System.out.println("ERROR WITH CREATING FILE OBJECT");
            this.setOpaque(false);
            this.draw();
        }
    }

    private void draw() {
        int height = 0;
        if (id != 0) {
            for (FileOption fO: this.pane.fO) {
                if (fO.id == this.id - 1) {
                    height = fO.getHeight();
                    break;
                }
            }
        }
        this.removeAll();
        JLabel n = new JLabel();
        n.setText(this.name);
        n.setBounds(15,height,this.Width,35);
        Font f = new Font("Courier", Font.BOLD,18);
        n.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        height = height + 40;
        this.add(n);
        int count = 0;
        if (this.tables.size() < 1) {
            JLabel noTables = new JLabel();
            noTables.setText("No tables detected");
            noTables.setBounds(15,height,this.Width,35);
            noTables.setForeground(Color.RED);
            Font f2 = new Font("Courier", Font.BOLD,13);
            noTables.setFont(f2.deriveFont(f.getStyle() ^ Font.BOLD));
            this.add(noTables);
            height = height + 50;
            System.out.println("No tables found");
        } else {
            for (ParseTable pt : this.tables) {
                JCheckBox enable = new JCheckBox();
                final int finalCount = count;
                enable.setSelected(true);
                enable.addItemListener(e -> this.toggle(finalCount));
                enable.setBounds(5, height+10, 20, 20);
                enable.setBackground(Color.WHITE);
                this.add(enable);
                count++;
                String[] headers = pt.getHeaderNames();
                String[][] content = pt.getColumnAttributes();
                JTable tab = new JTable(content, headers);
                JTableHeader Tabheaders = tab.getTableHeader();
                this.tab.add(tab);
                this.Tabheaders.add(Tabheaders);
                tab.setEnabled(false);
                MouseListener mL = new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            new TableViewWindow(pt);
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                };
                tab.addMouseListener(mL);
                Tabheaders.addMouseListener(mL);
                int decidedWidth = this.Width - 80;
                if (this.pane.getWidth() - 30 > headers.length * 155) {
                    decidedWidth = headers.length * 150;
                }
                Tabheaders.setBounds(30, height, decidedWidth, 20);
                tab.setBounds(30, height + 20, decidedWidth, 30);
                ArrayList<Problem> problems = pt.getProblems();
                if (problems.size() > 0) {
                    height = height + 45;
                    for (Problem p : problems) {
                        JLabel e = new JLabel();
                        e.setText(p.getTitle());
                        e.setBounds(30, height, decidedWidth, 15);
                        this.add(e);
                        height = height + 18;
                    }
                } else {
                    height = height + 50;
                }
                this.add(Tabheaders);
                this.add(tab);
                height = height + 10;
            }
        }
        this.height = height;
        //this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED));
        this.setBounds(0,0,this.pane.getWidth(),height);
        this.setBackground(Color.white);
        this.setLayout(null);
        this.setVisible(true);
    }

    void resize() {
        if (this.tables.size() >= 1) {
            for (int i = 0; i < this.tab.size(); i++) {
                int decidedWidth = this.pane.getWidth() - 80;
                if (this.pane.getWidth() - 30 > this.tables.get(i).getColumnCount() * 155) {
                    decidedWidth = this.tables.get(i).getColumnCount() * 150;
                }
                JTable tab = this.tab.get(i);
                JTableHeader header = this.Tabheaders.get(i);
                tab.setBounds(tab.getX(), tab.getY(), decidedWidth, tab.getHeight());
                header.setBounds(header.getX(), header.getY(), decidedWidth, header.getHeight());
            }
            this.setBounds(0, 0, this.pane.getWidth(), height);
        }
    }

    private void toggle(int id) {
        this.active.set(id,!this.active.get(id));
        MainWindow.tableCount();
    }

    ArrayList<ParseTable> getTables() {
        return this.tables;
    }

    ArrayList<ParseTable> getActiveTables() {
        ArrayList<ParseTable> result = new ArrayList<>();
        for (int i = 0; i < this.active.size(); i++) {
            if (this.active.get(i)) {
                result.add(this.tables.get(i));
            }
        }
        return result;
    }

    int tableCount() {
        int count = 0;
        for (boolean b: this.active) {
            if (b) {
                count++;
            }
        }
        return count;
    }
}
