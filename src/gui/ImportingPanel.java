package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

class ImportingPanel extends JPanel {
    private String previousFile;
    private JPanel files;
    private JScrollPane scrollable;
    private int counter;
    ArrayList<FileOption> fO;

    ImportingPanel() {
        this.fO = new ArrayList<>();
        this.counter = 1;
        JButton addFile = new JButton("Add file");
        addFile.setBounds(10,10,85,25);
        addFile.addActionListener(e -> {
            JFileChooser fc;
            if (this.previousFile == null) {
                fc = new CustomFileChooser();
            } else {
                fc = new CustomFileChooser(this.previousFile);
            }
            fc.setLocation(20,20);
            int choice = fc.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                this.previousFile = fc.getSelectedFile().getAbsolutePath();
                FileOption fo = new FileOption(this.previousFile,this,counter);
                this.counter++;
                MainWindow.files.add(fo);
                MainWindow.tableCount();
                this.fO.add(fo);
                files.add(fo);
                int w = MainWindow.main.getWidth();
                int totalheight = 0;
                for (FileOption fO: fO) {
                    totalheight = fO.resize();
                }
                files.setPreferredSize(new Dimension(w-40,totalheight));
                this.updateUI();
            }
        });
        this.add(addFile);
        JButton clear = new JButton("Remove all");
        clear.setBounds(100,10,100,25);
        clear.addActionListener(e -> {
            this.counter = 1;
            this.fO = new ArrayList<>();
            this.files.removeAll();
            MainWindow.removeAll();
            this.files.setPreferredSize(new Dimension(0,0));
            this.updateUI();
            this.repaint();
        });
        this.add(clear);
        files = new JPanel();
        files.setPreferredSize(new Dimension(460,650));
        files.setBackground(Color.WHITE);
        files.setLayout(null);
        this.scrollable = new JScrollPane(files);
        this.scrollable.setBounds(10,40,460,650);
        this.add(this.scrollable);
        this.setLayout(null);
        MainWindow.main.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int h = MainWindow.main.getHeight();
                int w = MainWindow.main.getWidth();
                int totalheight = 0;
                for (FileOption fO: fO) {
                    totalheight = fO.resize();
                }
                files.setPreferredSize(new Dimension(w-40,totalheight));
                scrollable.setBounds(10,40,w-35,h-115);
                MainWindow.main.revalidate();
            }
        });
    }
}
