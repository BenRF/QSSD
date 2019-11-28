package gui;

import parse.ParseTable;

import javax.swing.*;
import java.util.ArrayList;

class MergingPanel extends JPanel {
    private int step;
    private ArrayList<ParseTable> tabs;
    private JButton forward,back;

    MergingPanel() {
        this.setLayout(null);
        this.setup();
    }

    void setup() {
        this.removeAll();
        back = new JButton("<<");
        back.setBounds(20,550,100,30);
        back.addActionListener(e -> stepBack());
        forward = new JButton(">>");
        forward.setEnabled(false);
        forward.setBounds(360,550,100,30);
        forward.addActionListener(e -> stepForward());
        this.add(back);
        this.add(forward);

        this.tabs = MainWindow.getTables();
        this.step = this.tabs.size();
    }

    private void stepForward() {
        this.step++;
        if (this.step > 1) {
            this.back.setEnabled(true);
        }
        if (this.step == this.tabs.size()) {
            this.forward.setEnabled(false);
        }
        System.out.println(this.step);
    }

    private void stepBack() {
        this.step--;
        if (this.step < this.tabs.size()) {
            this.forward.setEnabled(true);
        }
        if (this.step == 1) {
            this.back.setEnabled(false);
        }
        System.out.println(this.step);
    }

    void pause() {
        this.removeAll();
    }
}
