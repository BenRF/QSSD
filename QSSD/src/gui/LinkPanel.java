package gui;

import parse.Link;
import parse.ParseTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class LinkPanel extends JPanel {
    private ArrayList<Link> links;
    private boolean altered;

    public LinkPanel() {
        this.setLayout(null);
        this.setBounds(30,113,MainWindow.getWidth()-80,88);
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delete(e);
            }

            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });
    }

    void delete(MouseEvent e) {
        int remove = -1;
        for (int i = 0; i < this.links.size(); i++) {
            if (this.links.get(i).isClicked(e.getX(),e.getY())) {
                remove = i;
                break;
            }
        }
        if (remove != -1) {
            this.links.remove(remove);
            this.altered = true;
            SwingUtilities.getWindowAncestor(this).repaint();
        }
    }

    public void paint(Graphics g, ParseTable before, ParseTable mergingWith, ArrayList<Link> links) {
        this.links = links;
        this.altered = false;
        int width = SwingUtilities.getWindowAncestor(this).getWidth()-80;
        int col1Width,col2Width;
        if (width - 30 > before.getColumnCount() * 155) {
            col1Width = 150;
        } else {
            col1Width = (width / before.getColumnCount());
        }
        if (width - 30 > mergingWith.getColumnCount() * 155) {
            col2Width = 150;
        } else {
            col2Width = (width / mergingWith.getColumnCount());
        }
        int newWidth = Math.max(before.getColumnCount()*col1Width,mergingWith.getColumnCount()*col2Width);
        this.setBounds(30,113,newWidth,87);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        float tab1,tab2;
        for (Link li: links) {
            tab1 = (float)(30 + (before.getColIdFromName(li.getFirstCol()) * col1Width) + (0.5 * col1Width));
            tab2 = (float)(30 + (mergingWith.getColIdFromName(li.getSecondCol()) * col2Width) + (0.5 * col2Width));
            g2.draw(li.getLine(tab1,tab2));
        }
    }

    public boolean hasAltered() {
        return this.altered;
    }

    public ArrayList<Link> getLinks() {
        return this.links;
    }
}
