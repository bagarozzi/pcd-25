package org;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GamePanel extends JPanel {
    private final Player player;
    JButton[][] buttons;

    public GamePanel(Player p) {
        super(new GridLayout(3, 3));
        this.player = p;
        buttons = new JButton[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                JButton btn = new JButton();
                btn.setFont(new Font("Arial", Font.BOLD, 40));

                // bordi per simulare la griglia
                btn.setBorder(new LineBorder(Color.BLACK, 2));

                // esempio click
                int row = i;
                int col = j;

                btn.addActionListener(_ -> {
                    if(buttons[row][col].getText().isEmpty()) {
                        btn.setText(player.getSign().toString());
                        new Thread(() -> {
                            p.makeMove(new Pair(row, col));
                        }).start();
                    }
                });

                buttons[i][j] = btn;
                this.add(btn);
            }
        }
    }

    public void update(Pair pos, Character sign) {
        buttons[pos.x()][pos.y()].setText(sign.toString());
    }




}
