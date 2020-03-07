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
        mP = new MergingPanel();
        jTP = new JTabbedPane();
        jTP.setBounds(0,0, main.getWidth()-2, main.getHeight()-2);
        jTP.add("Files",new ImportingPanel());
        jTP.add("Merging",mP);
        toggleTab(false);
        jTP.setVisible(true);
        jTP.addChangeListener(changeEvent -> {
            if (jTP.getSelectedIndex() != 0) {
                mP.setup();
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

    private static void toggleTab(boolean state) {
        jTP.setEnabledAt(1,state);
    }

    static void tableCount() {
        int count = 0;
        for(FileOption fO: files) {
            count = count + fO.tableCount();
        }
        boolean state = count > 1;
        toggleTab(state);
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

    public static void removeAll() {
        files = new ArrayList<>();
        tableCount();
    }
}
