package gui;

import files.ExcelFile;
import parse.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;

public class MainWindow {
    private JFrame main;
    static ArrayList<FileOption> files;
    JTabbedPane jTP;

    public MainWindow() {
        files = new ArrayList<>();
        this.main = new JFrame("QSSD");
        this.main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.main.setPreferredSize(new Dimension(500,700));
        this.main.setLayout(null);
        this.main.pack();
        this.main.setVisible(true);

        jTP = new JTabbedPane();
        jTP.setBounds(0,0,main.getWidth()-2,main.getHeight()-2);
        jTP.setBackground(Color.BLUE);
        jTP.add("Files",new ImportingPanel());
        this.main.add(jTP);

        JFrame main = this.main;
        ArrayList<FileOption> fOS = files;
        ComponentAdapter cA = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){

            }
        };
        this.main.addComponentListener(cA);
    }
}
