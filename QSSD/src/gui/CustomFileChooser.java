package gui;

import javax.swing.*;
import java.awt.*;

public class CustomFileChooser extends JFileChooser {
    private int x,y;

    public CustomFileChooser(int x, int y, String previousFile) {
        super(previousFile);
        this.x = x;
        this.y = y;
    }

    public CustomFileChooser(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected JDialog createDialog(Component parent) {
        JDialog dlg = super.createDialog(parent);
        dlg.setLocation(this.x + 20, this.y + 150);
        return dlg;
    }
}
