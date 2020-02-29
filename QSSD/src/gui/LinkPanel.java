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
    private LinkDragListener dragger;
    private ParseTable before,mergingWith;
    private int col1Width,col2Width;
    private boolean setup;
    private Line2D linkLine;

    public LinkPanel() {
        this.linkLine = new Line2D.Float(0,0,0,0);
        this.setup = false;
        this.setBackground(Color.ORANGE);
        this.dragger = new LinkDragListener(this);
        this.addMouseMotionListener(this.dragger);
        this.setLayout(null);
        this.setBounds(30,113,MainWindow.getWidth()-80,88);
        LinkPanel lP = this;
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delete(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                dragger.released();
                linkLine = new Line2D.Float(0,0,0,0);
                SwingUtilities.getWindowAncestor(lP).repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
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
        this.setup = true;
        this.links = links;
        this.altered = false;
        int width = SwingUtilities.getWindowAncestor(this).getWidth()-80;
        this.before = before;
        this.mergingWith = mergingWith;
        if (this.before != null) {
            if (width - 30 > before.getColumnCount() * 155) {
                this.col1Width = 150;
            } else {
                this.col1Width = (width / before.getColumnCount());
            }
            if (width - 30 > mergingWith.getColumnCount() * 155) {
                this.col2Width = 150;
            } else {
                this.col2Width = (width / mergingWith.getColumnCount());
            }
            this.paint(g);
        }
    }

    public void paint(Graphics g) {
        this.revalidate();
        this.removeAll();
        if (this.setup && this.before != null && links != null) {
            int newWidth = Math.max(before.getColumnCount() * col1Width, mergingWith.getColumnCount() * col2Width);
            this.setBounds(30, 113, newWidth, 87);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            float tab1, tab2;
            for (Link li : links) {
                tab1 = (float) (30 + (before.getColIdFromName(li.getFirstCol()) * this.col1Width) + (0.5 * this.col1Width));
                tab2 = (float) (30 + (mergingWith.getColIdFromName(li.getSecondCol()) * this.col2Width) + (0.5 * this.col2Width));
                g2.draw(li.getLine(tab1, tab2));
            }
            g2.draw(this.linkLine);
        }
    }

    public boolean hasAltered() {
        return this.altered;
    }

    public ArrayList<Link> getLinks() {
        if (this.links == null || this.links.size() == 0) {
            return null;
        } else {
            return this.links;
        }
    }

    public void updateLine(int x1,int y1, int x2, int y2) {
        this.linkLine = new Line2D.Float(x1,y1,x2,y2);
        SwingUtilities.getWindowAncestor(this).repaint();
    }

    public void newLink(int x1,int x2) {
        this.linkLine = new Line2D.Float(0,0,0,0);
        String col1 = this.before.getColumnName(x1 / col1Width);
        String col2 = this.mergingWith.getColumnName(x2 / col2Width);
        if (col1.length() > 0 && col2.length() > 0) {
            boolean sameName = col1.equals(col2);
            int[] contentOverlap = this.before.getColumnFromName(col1).checkContent(this.mergingWith.getColumnFromName(col2));
            System.out.println("LINKING " + col1 + " with " + col2);
            this.links.add(new Link(col1,col2,sameName,contentOverlap[0],contentOverlap[1]));
            this.altered = true;
            SwingUtilities.getWindowAncestor(this).repaint();
        }
    }
}
