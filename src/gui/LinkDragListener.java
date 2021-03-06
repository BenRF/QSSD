package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class LinkDragListener implements MouseMotionListener {
    private boolean dragging,downwards;
    private LinkPanel p;
    private int x1,y1,x2,y2,count;

    public LinkDragListener(LinkPanel p) {
        this.dragging = false;
        this.p = p;
        this.count = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragging) {
            if (e.getY() > 15) {
                dragging = true;
                downwards = true;
                x1 = e.getX();
                y1 = e.getY();
            } else if (e.getY() > 73) {
                dragging = true;
                downwards = false;
                x1 = e.getX();
                y1 = e.getY();
            }
        } else {
            this.count++;
            this.x2 = e.getX();
            this.y2 = e.getY();
            this.p.updateLine(this.x1,this.y1,this.x2,this.y2);
        }

    }

    void released() {
        this.dragging = false;
        if (((y2 > 73 && downwards) || (y2 < 15 && !downwards)) && this.count > 5) {
            this.p.newLink(this.x1,this.x2);
        }
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.count = 0;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
