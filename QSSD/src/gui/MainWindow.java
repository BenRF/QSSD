package gui;

import parse.ParseTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class MainWindow {
    static ArrayList<FileOption> files;
    private JTabbedPane jTP;

    public MainWindow() {
        files = new ArrayList<>();
        JFrame main = new JFrame("QSSD");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setPreferredSize(new Dimension(500,700));
        main.setLayout(null);
        main.pack();
        main.setVisible(true);
        OutputPanel oP = new OutputPanel();
        jTP = new JTabbedPane();
        jTP.setBounds(0,0, main.getWidth()-2, main.getHeight()-2);
        jTP.add("Files",new ImportingPanel(this));
        jTP.add("Output", oP);
        this.toggleTab(1,false);
        jTP.addChangeListener(e -> {
            if (jTP.getSelectedIndex() == 1) {
                oP.update();
            }
        });
        main.add(jTP);
        ComponentAdapter cA = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        };
        main.addComponentListener(cA);
    }

    void toggleTab(int id, boolean state) {
        this.jTP.setEnabledAt(id,state);
    }

    int tableCount() {
        int count = 0;
        for(FileOption pT: files) {
            count = count + pT.tableCount();
        }
        return count;
    }
}
