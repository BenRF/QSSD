package gui;

import files.CSVFile;
import files.ExcelFile;
import parse.ParseTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class OutputPanel extends JPanel {

    void setup() {
        this.removeAll();
        JLabel name = new JLabel("File name:");
        name.setBounds(20,500,100,30);
        name.setFont(name.getFont().deriveFont(15.0f));
        JTextField nameInput = new JTextField();
        nameInput.setFont(nameInput.getFont().deriveFont(15.0f));
        nameInput.setBounds(115,500,200,30);
        String[] options = {".xlsx",".csv"};
        JComboBox<String> extension = new JComboBox<>(options);
        extension.setFont(extension.getFont().deriveFont(15.0f));
        extension.setBounds(325,500,100,30);
        this.add(extension);
        this.add(name);
        this.add(nameInput);
        ParseTable result = MergingPanel.getResult();
        JTable table = result.getSummaryJTable();
        JTableHeader header = result.getJTableHeader(table);
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String name = table.getColumnName(col);
                result.sortByColName(name);
            }
        });
        int pos = 200;
        int decidedWidth = getWidth() - 80;
        header.setBounds(30, pos, decidedWidth, 20);
        table.setBounds(30, pos+20, decidedWidth, 33);
        this.add(table);
        this.add(header);
        JButton merge = new JButton("Save");
        merge.setBounds(150,550,100,30);
        merge.addActionListener(e -> {
            switch (extension.getSelectedIndex()) {
                case (0):
                    //EXCEL
                    new ExcelFile(result,nameInput.getText());
                    break;
                case (1):
                    //CSV
                    new CSVFile(result,nameInput.getText());
                    break;
            }
        });
        this.add(merge);
        this.setLayout(null);
    }
}
