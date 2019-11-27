package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class MainWindow {
    static ArrayList<FileOption> files;
    private static JTabbedPane jTP;

    public MainWindow() {
        files = new ArrayList<>();
        JFrame main = new JFrame("QSSD");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setPreferredSize(new Dimension(500,700));
        OutputPanel oP = new OutputPanel();
        jTP = new JTabbedPane();
        jTP.setBounds(0,0, main.getWidth()-2, main.getHeight()-2);
        jTP.add("Files",new ImportingPanel(this));
        jTP.add("Output", oP);
        toggleTab(1,false);
        jTP.addChangeListener(e -> {
            if (jTP.getSelectedIndex() == 1) {
                oP.update();
            }
        });
        jTP.setVisible(true);
        main.add(jTP);
        ComponentAdapter cA = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        };
        main.addComponentListener(cA);
        //main.setLayout(null);
        main.pack();
        main.setVisible(true);
    }

    private static void toggleTab(int id, boolean state) {
        jTP.setEnabledAt(id,state);
    }

    static int tableCount() {
        int count = 0;
        for(FileOption fO: files) {
            count = count + fO.tableCount();
        }
        toggleTab(1,count > 1);
        return count;
    }
}
