package gui;

import javax.swing.*;

public class StepButton {
    private JButton button;
    public StepButton(String name, int step, MergingPanel p, int width) {
        this.button = new JButton(name);
        this.button.addActionListener(e -> p.goToStep(step));
        this.button.setBounds(30+(width*step),20,width,30);
    }

    JButton getButt() {
        return this.button;
    }

    void setEnabled(boolean enabled) {
        this.button.setEnabled(enabled);
    }
}
