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
        back.setBounds(20,550,100,30);
        back.addActionListener(e -> stepBack());
        forward = new JButton("Forward");
        forward.setEnabled(false);
        forward.setBounds(360,550,100,30);
        forward.addActionListener(e -> stepForward());
        this.add(back);
        this.add(forward);

        this.tabs = MainWindow.getTables();
        this.step = this.tabs.size()-1;
        update();
    }

    void update() {
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
        String[] headers = pT.getHeaderNames();
        String[][] content = pT.getContent();
        JTable jT = new JTable(content, headers);
        JTableHeader header = jT.getTableHeader();
        int decidedWidth = this.getWidth()-80;
        if (this.getWidth()-30 > headers.length * 155) {
            decidedWidth = headers.length * 150;
        }
        header.setBounds(30, 200, decidedWidth, 20);
        jT.setBounds(30, 220, decidedWidth, 16 * content.length);
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

    void pause() {
        this.removeAll();
    }
}
