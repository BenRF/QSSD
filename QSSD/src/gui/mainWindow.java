package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class mainWindow {
    private JFrame main;
    private ArrayList<fileOption> files;
    private JPanel sp;

    public mainWindow() {
        this.files = new ArrayList<>();
        this.main = new JFrame("QSSD");
        this.main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton addFile = new JButton("New File");
        addFile.setBounds(10,10,85,25);
        addFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int choice = fc.showOpenDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                fileOption fo = new fileOption(fc.getSelectedFile().getAbsolutePath(),this.files.size());
                this.files.add(fo);
                this.sp.add(fo);
                //this.sp.revalidate();
                this.sp.updateUI();
            }
        });
        this.main.add(addFile);

        this.sp = new JPanel();
        this.sp.setBounds(25,50,430,500);
        this.sp.setBackground(Color.WHITE);
        this.sp.setLayout(new BoxLayout(this.sp, BoxLayout.Y_AXIS));
        main.add(this.sp);

        this.main.setPreferredSize(new Dimension(500,700));
        this.main.setLayout(null);
        this.main.pack();
        this.main.setVisible(true);
    }
}
