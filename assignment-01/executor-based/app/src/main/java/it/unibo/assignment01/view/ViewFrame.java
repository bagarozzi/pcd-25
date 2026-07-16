package it.unibo.assignment01.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.util.RenderSynch;

public class ViewFrame extends JFrame {

    private final PooolPanel panel;
    private final RenderSynch sync;

    public ViewFrame(int width, int height) {
        setTitle("Poool");
        setSize(width, height + 25);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        sync = new RenderSynch();

        panel = new PooolPanel(width, height);
        panel.setFocusable(true);
        getContentPane().add(panel);
    }

    public void updateView(ViewModel viewModel, final long frameNumber) {
        panel.updateViewModel(viewModel, frameNumber);
    }

    public PooolPanel getPanel() {
        return panel;
    }

    public void showEndGame(String winner) {
        SwingUtilities.invokeLater(() -> {
           JOptionPane.showMessageDialog(this, "Game Over! " + winner); 
           System.exit(0);
        });
    }

    public class PooolPanel extends JPanel {
        private ViewModel viewModel;
        private final int ox;
        private final int oy;
        private final int delta;
        private long fps = 0;
        
        // Double-buffering
        private BufferedImage backBuffer;
        private BufferedImage frontBuffer;
        private final Object bufferLock = new Object();
        
        // Key state tracking - more efficient than Set
        private volatile boolean[] keys = new boolean[4];
        private static final int UP = 0;
        private static final int DOWN = 1;
        private static final int LEFT = 2;
        private static final int RIGHT = 3;

        public PooolPanel(int w, int h) {
            setSize(w,h + 25);
            ox = w/2;
            oy = h/2;
            delta = Math.min(ox, oy);
            setBackground(Color.WHITE);
            
            // Initialize buffers
            backBuffer = new BufferedImage(w, h + 25, BufferedImage.TYPE_INT_RGB);
            frontBuffer = new BufferedImage(w, h + 25, BufferedImage.TYPE_INT_RGB);
            
            this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        keys[UP] = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        keys[DOWN] = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        keys[LEFT] = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        keys[RIGHT] = true;
                        break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        keys[UP] = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        keys[DOWN] = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        keys[LEFT] = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        keys[RIGHT] = false;
                        break;
                }
            }
        });
        }

        public boolean[] getPressedKeys() {
            return keys;
        }

        public void updateViewModel(ViewModel vm, final long frameNumber) {
            long nf = sync.nextFrameToRender();
            this.viewModel = vm;
            fps = frameNumber;
            repaint(0L);
            try {
			    sync.waitForFrameRendered(nf);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Render to back buffer
            renderToBuffer();
            
            // Swap buffers
            swapBuffers();
            
            // Draw front buffer to screen
            synchronized(bufferLock) {
                g.drawImage(frontBuffer, 0, 0, this);
            }
        }
        
        private void renderToBuffer() {
            Graphics2D g2d = backBuffer.createGraphics();
            
            try {
                // Set rendering hints for quality
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Clear buffer with white background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
                
                // 1. Draw cross grid (light gray thin lines)
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(ox, 0, ox, oy * 2); // Vertical line
                g2d.drawLine(0, oy, ox * 2, oy); // Horizontal line

                // 2. Draw two black holes (top corners)
                int holeRadius = (int)(BoardImpl.HOLE_RADIUS * delta);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(-holeRadius, -holeRadius, holeRadius * 2, holeRadius * 2);
                g2d.fillOval(ox*2 - holeRadius, -holeRadius, holeRadius * 2, holeRadius * 2);

                if (viewModel == null) return;

                // 3. Draw Scores (large, blue, in lower quadrants)
                g2d.setColor(Color.BLUE);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 120));
                FontMetrics fmScores = g2d.getFontMetrics();

                String humanScoreStr = String.valueOf(viewModel.getHumanScore());
                String botScoreStr = String.valueOf(viewModel.getBotScore());

                int hScoreX = (ox / 2) - (fmScores.stringWidth(humanScoreStr) / 2);
                int bScoreX = (ox * 3 / 2) - (fmScores.stringWidth(botScoreStr) / 2);
                int scoreY = oy * 3 / 2;

                g2d.drawString(humanScoreStr, hScoreX, scoreY);
                g2d.drawString(botScoreStr, bScoreX, scoreY);

                // 4. Draw small balls (white with black border)
                List<Ball> smallBalls = viewModel.getSmallBalls();
                if (smallBalls != null) {
                    for (Ball ball : smallBalls) {
                        drawBall(g2d, ball, new BasicStroke(1), Optional.empty(), Optional.empty());
                    }
                }

                // 5. Draw player balls (H and B)
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fmPlayers = g2d.getFontMetrics();

                // Draw H (Human)
                Ball humanBall = viewModel.getHumanBall();
                if (humanBall != null) {
                    drawBall(g2d, humanBall, new BasicStroke(3), Optional.of("H"), Optional.of(fmPlayers));
                }

                // Draw B (Bot)
                Ball botBall = viewModel.getBotBall();
                if (botBall != null) {
                    drawBall(g2d, botBall, new BasicStroke(3), Optional.of("B"), Optional.of(fmPlayers));
                }

                g2d.drawString("Balls remaining: " + viewModel.getSmallBalls().size(), 15, oy*2 - 40);
                g2d.drawString("FPS: " + fps, ox, 30);

                sync.notifyFrameRendered();
            } finally {
                g2d.dispose();
            }
        }
        
        private void swapBuffers() {
            synchronized(bufferLock) {
                BufferedImage temp = frontBuffer;
                frontBuffer = backBuffer;
                backBuffer = temp;
            }
        }

        private void drawBall(Graphics2D g2d, Ball ball, Stroke s, Optional<String> text, Optional<FontMetrics> fm) {
            Position p = ball.getPos();
            int x0 = (int)(ox + p.x()*delta);
            int y0 = (int)(oy - p.y()*delta);
            int radiusX = (int)(ball.getRadius()*delta);
            int radiusY = (int)(ball.getRadius()*delta);
            g2d.setStroke(s);
            g2d.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
            if (text.isPresent() && fm.isPresent()) {
                int textX = x0 - (fm.get().stringWidth(text.get()) / 2);
                int textY = y0 + (fm.get().getAscent() / 2) - 2; // leggero aggiustamento ottico
                g2d.drawString(text.get(), textX, textY);
            }
        }

    }
}