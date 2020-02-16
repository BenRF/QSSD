package gui;

import parse.Link;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

    public void paint(Graphics g, int col1Width, int col2Width, ArrayList<Link> links, int newWidth) {
        this.links = links;
        this.altered = false;
        this.setBounds(30,113,newWidth,87);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        for (Link li: links) {
            g2.draw(li.getLine(col1Width,col2Width));
        }
    }

    public boolean hasAltered() {
        return this.altered;
    }

    public ArrayList<Link> getLinks() {
        return this.links;
    }
}
