package gui;

import files.CSVFile;
import files.ExcelFile;
import parse.ParseTable;

import javax.swing.*;
import java.util.ArrayList;

class OutputPanel extends JPanel {

    OutputPanel() {
        JLabel name = new JLabel("File name:");
        name.setBounds(20,10,100,30);
        name.setFont(name.getFont().deriveFont(15.0f));
        JTextField nameInput = new JTextField();
        nameInput.setFont(nameInput.getFont().deriveFont(15.0f));
        nameInput.setBounds(115,10,200,30);
        String[] options = {".xlsx",".csv"};
        JComboBox<String> extension = new JComboBox<>(options);
        extension.setFont(extension.getFont().deriveFont(15.0f));
        extension.setBounds(325,10,100,30);
        this.add(extension);
        this.add(name);
        this.add(nameInput);

        JButton merge = new JButton("Merge");
        merge.setBounds(180,600,100,30);
        merge.addActionListener(e -> {
            ArrayList<ParseTable> pTS = new ArrayList<>();
            for (FileOption fO: MainWindow.files) {
                pTS.addAll(fO.getTables());
            }
            ParseTable result = new ParseTable(pTS.get(0),pTS.get(1));
            for (int i = 2; i < pTS.size(); i++) {
                result = new ParseTable(result,pTS.get(i));
            }
            result.toConsole();
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
