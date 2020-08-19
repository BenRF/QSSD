package gui;

import javax.swing.*;
import java.awt.*;

public class CustomFileChooser extends JFileChooser {

    public CustomFileChooser(String prevFile) {
        super(prevFile);
    }

    public CustomFileChooser() {
        super();
    }

    protected JDialog createDialog(Component parent) {
        JDialog dlg = super.createDialog(parent);
        dlg.setLocation(MainWindow.getProgramXPos() + (MainWindow.getWidth()/2) - (this.getWidth()/2),MainWindow.getProgramYPos() + (MainWindow.getHeight()/2) - (this.getHeight()/2));
        return dlg;
    }
}
