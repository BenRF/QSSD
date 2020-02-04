package parse;

import gui.TableViewWindow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TableMouseListener implements MouseListener {
    ParseTable table;

    public TableMouseListener(ParseTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            new TableViewWindow(table);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
