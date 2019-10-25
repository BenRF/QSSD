package gui;

import parse.parseTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class mainWindow {
    private JFrame main;
    private ArrayList<fileOption> files;
    private JPanel sp;
    private String previousFile;

    public mainWindow() {
        this.files = new ArrayList<>();
        this.previousFile = null;
        this.main = new JFrame("QSSD");
        this.main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton addFile = new JButton("New File");
        addFile.setBounds(10,10,85,25);
        addFile.addActionListener(e -> {
            JFileChooser fc;
            if (this.previousFile == null) {
                fc = new JFileChooser();
            } else {
                fc = new JFileChooser(this.previousFile);
            }
            int choice = fc.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                this.previousFile = fc.getSelectedFile().getAbsolutePath();
                fileOption fo = new fileOption(this.previousFile);
                for (parseTable pT: fo.getTables()) {
                    for (int i = 0; i < pT.rowCount(); i++) {
                        System.out.println(pT.getRow(i));
                    }
                    System.out.println();
                }
                this.files.add(fo);
                this.sp.add(fo);
                this.sp.updateUI();
            }
        });
        this.main.add(addFile);
        JButton mergeFile = new JButton("Merge");
        mergeFile.setBounds(150,10,85,25);
        mergeFile.addActionListener(e -> {
            ArrayList<parseTable> tables =  new ArrayList<>();
            for (fileOption fO: this.files) {
                tables.addAll(fO.getTables());
            }
            parseTable output;
            if (tables.size() < 3) {
                output = new parseTable(tables.get(0),tables.get(1));
            } else {
                output = null;
                for (parseTable pT: tables) {
                    if (output == null) {
                        output = new parseTable(pT);
                    } else {
                        output = new parseTable(output,pT);
                    }
                }
            }
            if (output != null) {
                for (int i = 0; i < output.rowCount(); i++) {
                    System.out.println(output.getRow(i));
                }
            }
            fileOption fo = new fileOption(output);
            this.files.add(fo);
            this.sp.add(fo);
            this.sp.updateUI();
        });
        this.main.add(mergeFile);

        this.sp = new JPanel();
        this.sp.setBounds(25,50,430,500);
        this.sp.setBackground(Color.WHITE);
        this.sp.setLayout(new FlowLayout());
        main.add(this.sp);

        this.main.setPreferredSize(new Dimension(500,700));
        this.main.setLayout(null);
        this.main.pack();
        this.main.setVisible(true);
    }
}
