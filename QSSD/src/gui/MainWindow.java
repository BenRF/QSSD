package gui;

import parse.ParseTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class MainWindow {
    static ArrayList<FileOption> files;
    private static JTabbedPane jTP;
    private static MergingPanel mP;
    static JFrame main;

    public MainWindow() {
        files = new ArrayList<>();
        main = new JFrame("QSSD");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setPreferredSize(new Dimension(500,700));
        OutputPanel oP = new OutputPanel();
        mP = new MergingPanel();
        jTP = new JTabbedPane();
        jTP.setBounds(0,0, main.getWidth()-2, main.getHeight()-2);
        jTP.add("Files",new ImportingPanel());
        jTP.add("Merging",mP);
        jTP.add("Output", oP);
        toggleTab(1,false);
        toggleTab(2,false);
        jTP.setVisible(true);
        jTP.addChangeListener(changeEvent -> {
            if (jTP.getSelectedIndex() != 0) {
                mP.setup();
                oP.setup();
            }
        });
        main.add(jTP);
        ComponentAdapter cA = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        };
        main.addComponentListener(cA);
        main.pack();
        main.setVisible(true);
    }

    private static void toggleTab(int id, boolean state) {
        jTP.setEnabledAt(id,state);
    }

    static void tableCount() {
        int count = 0;
        for(FileOption fO: files) {
            count = count + fO.tableCount();
        }
        boolean state = count > 1;
        toggleTab(1,state);
        toggleTab(2,state);
    }

    static ArrayList<ParseTable> getTables() {
        ArrayList<ParseTable> result = new ArrayList<>();
        for (FileOption fO: files) {
            result.addAll(fO.getActiveTables());
        }
        return result;
    }

    static int getProgramXPos() {
        return main.getX();
    }

    static int getProgramYPos() {
        return main.getY();
    }

    static int getWidth() {
        return main.getWidth();
    }

    static int getHeight() {
        return main.getHeight();
    }
}
