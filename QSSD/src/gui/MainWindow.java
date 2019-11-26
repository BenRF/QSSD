package gui;

import parse.ParseTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class MainWindow {
    private JFrame main;
    static ArrayList<FileOption> files;
    private OutputPanel oP;
    private JTabbedPane jTP;

    public MainWindow() {
        files = new ArrayList<>();
        this.main = new JFrame("QSSD");
        this.main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.main.setPreferredSize(new Dimension(500,700));
        this.main.setLayout(null);
        this.main.pack();
        this.main.setVisible(true);
        OutputPanel oP = new OutputPanel();
        this.oP = oP;
        jTP = new JTabbedPane();
        jTP.setBounds(0,0,main.getWidth()-2,main.getHeight()-2);
        jTP.add("Files",new ImportingPanel(this));
        jTP.add("Output",this.oP);
        this.toggleTab(1,false);
        jTP.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (jTP.getSelectedIndex() == 1) {
                    oP.update();
                }
            }
        });
        this.main.add(jTP);
        JFrame main = this.main;
        ArrayList<FileOption> fOS = files;
        ComponentAdapter cA = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        };
        this.main.addComponentListener(cA);
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
