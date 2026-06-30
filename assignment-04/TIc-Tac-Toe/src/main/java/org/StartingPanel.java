package org;

import javax.swing.*;
import java.awt.*;

public class StartingPanel extends JPanel {
    public StartingPanel(Player p, GameFrame gp) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        JLabel label = new JLabel("Welcome to TAC Toe");
        label.setSize(300, 50);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 40));
        JPanel bottom = new JPanel(new FlowLayout());
        JButton btn = new JButton("Find Game");
        btn.setPreferredSize(new Dimension(200, 50));

        btn.addActionListener((_) -> {
            p.joinGame();
            gp.setWaitingPanel();
        });

        bottom.add(btn);
        this.add(label,  BorderLayout.CENTER);
        this.add(bottom,  BorderLayout.SOUTH);
    }
}
