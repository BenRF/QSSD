package gui;

import files.ExcelFile;
import parse.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow {
    private JFrame main;
    private ArrayList<FileOption> files;
    private JPanel sp;
    private String previousFile;

    public MainWindow() {
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
                FileOption fo = new FileOption(this.previousFile);
                for (ParseTable pT: fo.getTables()) {
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
        mergeFile.setBounds(100,10,85,25);
        mergeFile.addActionListener(e -> {
            ArrayList<ParseTable> tables =  new ArrayList<>();
            for (FileOption fO: this.files) {
                tables.addAll(fO.getTables());
            }
            ParseTable output;
            if (tables.size() < 3) {
                output = new ParseTable(tables.get(0),tables.get(1));
            } else {
                output = null;
                for (ParseTable pT: tables) {
                    if (output == null) {
                        output = new ParseTable(pT);
                    } else {
                        output = new ParseTable(output,pT);
                    }
                }
            }
            if (output != null) {
                for (int i = 0; i < output.rowCount(); i++) {
                    System.out.println(output.getRow(i));
                }
            }
            FileOption fo = new FileOption(output);
            this.files.add(fo);
            this.sp.add(fo);
            this.sp.updateUI();
        });
        this.main.add(mergeFile);

        JButton output = new JButton("Output");
        output.setBounds(190,10,85,25);
        output.addActionListener(e -> {
            ParseTable pT = null;
            for (FileOption fO: this.files) {
                if (fO.getFileName().equals("OUTPUT")) {
                    pT = fO.getTable(0);
                }
            }
            if (pT != null) {
                ExcelFile f = new ExcelFile(pT);
            }
        });
        this.main.add(output);

        JButton clear = new JButton("Clear");
        clear.setBounds(280,10,85,25);
        clear.addActionListener(e -> {
            this.files = new ArrayList<>();
            this.sp.removeAll();
            this.sp.updateUI();
        });
        this.main.add(clear);

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
