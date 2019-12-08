package gui;

import parse.ParseTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

class ImportingPanel extends JPanel {
    private String previousFile;
    private JPanel files;
    private int counter;
    ArrayList<FileOption> fO;

    ImportingPanel() {
        this.fO = new ArrayList<>();
        this.counter = 0;
        JButton addFile = new JButton("Add file");
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
                FileOption fo = new FileOption(this.previousFile,this,counter);
                this.counter++;
                MainWindow.files.add(fo);
                MainWindow.tableCount();
                this.fO.add(fo);
                files.add(fo);
                this.updateUI();
            }
        });
        this.add(addFile);

        files = new JPanel();
        files.setBounds(10,40,460,550);
        files.setBackground(Color.WHITE);
        files.setLayout(null);
        this.add(files);
        this.setLayout(null);
        MainWindow.main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int w = MainWindow.main.getWidth();
                int h = MainWindow.main.getHeight();
                files.setBounds(10,40,w-40,h-140);
                for (FileOption fO: fO) {
                    fO.resize();
                }
                MainWindow.main.revalidate();
            }
        });
    }
}
