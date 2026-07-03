package org.gui;

import javax.swing.*;
import java.awt.*;

public class WaitingPanel extends JPanel {

    public WaitingPanel() {
        super(new GridBagLayout());
        this.setBackground(new Color(0, 0, 0, 150));
        Label label = new Label("Waiting for opponent");
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);
        label.setSize(new Dimension(100, 70));
        label.setBackground(Color.DARK_GRAY);
        this.add(label);
    }
}
