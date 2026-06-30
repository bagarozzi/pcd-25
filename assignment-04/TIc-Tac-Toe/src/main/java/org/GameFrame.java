package org;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final Player player;
    private final JPanel base;
    private final JPanel waitingPanel;
    private GamePanel gamePanel;
    private final JPanel startingPanel;

    public GameFrame(Player p) {
        this.player = p;
        this.gamePanel = new GamePanel(p);
        this.startingPanel = new StartingPanel(p,this);
        this.waitingPanel = new WaitingPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        base = new JPanel();
        base.setLayout(new OverlayLayout(base));
        base.add(startingPanel);
        this.add(base);
        this.setVisible(true);
    }

    public void update(Pair pos, Character sign) {
        gamePanel.update(pos, sign);
    }

    public void setWaitingPanel(){
        base.removeAll();
        for(Component c : gamePanel.getComponents()){
            c.setEnabled(false);
        }
        this.base.add(waitingPanel);
        this.base.add(gamePanel);

        this.revalidate();
        this.repaint();
    }

    public void setGamePanel(){
        base.remove(waitingPanel);
        this.revalidate();
        this.repaint();
    }

    public void blockMove(){
        for(Component c : gamePanel.getComponents()){
            c.setEnabled(false);
        }
        this.revalidate();
        this.repaint();
    }

    public void unlockMove(){
        for(Component c : gamePanel.getComponents()){
            c.setEnabled(true);
        }
        this.revalidate();
        this.repaint();
    }

    public void showEndGame(PlayerRemote.Result res) {
        SwingUtilities.invokeLater(() -> {

            blockMove();
            String message;
            if(res == PlayerRemote.Result.WIN){
                message = "You Win!";
            } else if(res == PlayerRemote.Result.LOSE){
                message = "You Lose";
            } else {
                message = "Draw";
            }

            int opt = JOptionPane.showConfirmDialog(
                    this,
                    message + "\nVuoi giocare ancora?",
                    "Fine partita",
                    JOptionPane.YES_NO_OPTION
            );

            if (opt == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }

        });
    }

    public void restartGame() {
        base.removeAll();
        gamePanel = new GamePanel(player);
        base.add(startingPanel);
        this.revalidate();
        this.repaint();
    }
}
