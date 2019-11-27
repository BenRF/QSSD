package gui;
import parse.*;
import files.*;
import parse.problems.Problem;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class FileOption extends JPanel {
    private String name;
    private ArrayList<ParseTable> tables;
    private List<Boolean> active;
    private int Width;
    private int id,height;
    private ImportingPanel pane;

    FileOption(String fileLocation,ImportingPanel pane,int id) {
        this.id = id;
        this.pane = pane;
        this.Width = this.pane.getWidth();
        TabSeperatedFile f = null;
        if (fileLocation.substring(fileLocation.length()-5).equals(".xlsx")) {
            f = new ExcelFile(fileLocation);
        } else if (fileLocation.substring(fileLocation.length()-4).equals(".csv")) {
            f = new CSVFile(fileLocation);
        }
        if (f != null) {
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
            this.tables = f.getTables();
            this.active = new ArrayList<>(Arrays.asList(new Boolean[this.tables.size()]));
            Collections.fill(this.active, Boolean.TRUE);
            System.out.println(this.active.toString());
            this.setOpaque(false);
            this.draw();
        } else {
            System.out.println("ERROR WITH CREATING FILE OBJECT");
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
        for (ParseTable pt: this.tables) {
            JCheckBox enable = new JCheckBox();
            final int finalCount = count;
            enable.setSelected(true);
            enable.addItemListener(e -> {
                this.toggle(finalCount);
            });
            enable.setBounds(5,height+10,20,20);
            enable.setBackground(Color.WHITE);
            this.add(enable);
            count++;
            String[] headers = pt.getHeaderNames();
            String[][] content = pt.getColumnAttributes();
            JTable jT = new JTable(content, headers);
            JTableHeader header = jT.getTableHeader();
            int decidedWidth = this.Width-80;
            if (this.pane.getWidth()-30 > headers.length * 155) {
                decidedWidth = headers.length * 150;
            }
            header.setBounds(30, height, decidedWidth, 20);
            jT.setBounds(30, height + 20, decidedWidth, 30);
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
            this.add(header);
            this.add(jT);
            height = height + 10;
        }
        //this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED));
        this.setBounds(0,0,this.pane.getWidth(),height);
        this.setBackground(Color.white);
        this.setLayout(null);
        this.setVisible(true);
        this.height = height;
    }

    private void toggle(int id) {
        this.active.set(id,!this.active.get(id));
        MainWindow.tableCount();
    }

    String getFileName() {
        return this.name;
    }

    int getH() {
        return this.height;
    }

    ArrayList<ParseTable> getTables() {
        return this.tables;
    }

    ParseTable getTable(int i) {
        return this.tables.get(i);
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
