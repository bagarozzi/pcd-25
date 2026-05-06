package it.unibo.assignment01.view;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.util.RenderSynch;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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
        getContentPane().add(panel);
    }

    public void updateView(ViewModel viewModel, final long frameNumber) {
        panel.updateViewModel(viewModel, frameNumber);
    }

    public PooolPanel getPanel() {
        return panel;
    }

    public class PooolPanel extends JPanel {
        private ViewModel viewModel;
        private final int ox;
        private final int oy;
        private final int delta;

        public PooolPanel(int w, int h) {
            setSize(w,h + 25);
            ox = w/2;
            oy = h/2;
            delta = Math.min(ox, oy);
            setBackground(Color.WHITE); // Sfondo bianco
        }

        public void updateViewModel(ViewModel vm, final long frameNumber) {
            long nf = sync.nextFrameToRender();
            this.viewModel = vm;
            repaint();
            try {
			    sync.waitForFrameRendered(nf);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Antialiasing per curve morbide
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // 1. Disegna la griglia a croce (linee grigie sottili)
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(ox, 0, ox, oy * 2); // Linea verticale
            g2d.drawLine(0, oy, ox * 2, oy); // Linea orizzontale

            // 2. Disegna le due buche nere (angoli superiori)
            int holeRadius = 60;
            g2d.setColor(Color.BLACK);
            g2d.fillOval(-holeRadius, -holeRadius, holeRadius * 2, holeRadius * 2); // Buca sx
            g2d.fillOval(ox*2 - holeRadius, -holeRadius, holeRadius * 2, holeRadius * 2); // Buca dx

            if (viewModel == null) return;

            // 3. Disegna i Punteggi (Grandi, blu, nei quadranti inferiori)
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 120));
            FontMetrics fmScores = g2d.getFontMetrics();

            String humanScoreStr = String.valueOf(viewModel.getHumanScore());
            String botScoreStr = String.valueOf(viewModel.getBotScore());

            // Posiziona i punteggi a 1/4 e 3/4 della larghezza, a circa 3/4 dell'altezza
            int hScoreX = (ox / 2) - (fmScores.stringWidth(humanScoreStr) / 2);
            int bScoreX = (ox * 3 / 2) - (fmScores.stringWidth(botScoreStr) / 2);
            int scoreY = oy * 3 / 2;

            g2d.drawString(humanScoreStr, hScoreX, scoreY);
            g2d.drawString(botScoreStr, bScoreX, scoreY);

            // 4. Disegna le migliaia di Palline Piccole (bianche con bordo nero)
            List<Ball> smallBalls = viewModel.getSmallBalls();
            if (smallBalls != null) {
                for (Ball ball : smallBalls) {
                    Position p = ball.getPos();
	            	int x0 = (int)(ox + p.x()*delta);
	                int y0 = (int)(oy - p.y()*delta);
                    int radiusX = (int)(ball.getRadius()*delta);
	                int radiusY = (int)(ball.getRadius()*delta);
                    g2d.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
                }
            }

            // 5. Disegna le Palline dei Giocatori (H e B)
            int bigRadius = 18;
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fmPlayers = g2d.getFontMetrics();

            // Disegna H (Human)
            Position humanBall = viewModel.getHumanBall();
            if (humanBall != null) {
                drawPlayerBall(g2d, humanBall, bigRadius, "H", fmPlayers);
            }

            // Disegna B (Bot)
            Position botBall = viewModel.getBotBall();
            if (botBall != null) {
                drawPlayerBall(g2d, botBall, bigRadius, "B", fmPlayers);
            }

            sync.notifyFrameRendered();
        }

        // Metodo di supporto per disegnare le palline grandi con la lettera centrata
        private void drawPlayerBall(Graphics2D g2d, Position pos, int radius, String text, FontMetrics fm) {
            int cx = (int) pos.x();
            int cy = (int) pos.y();

            // Sfondo bianco
            g2d.setColor(Color.WHITE);
            g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Bordo spesso nero
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

            // Lettera centrata
            int textX = cx - (fm.stringWidth(text) / 2);
            int textY = cy + (fm.getAscent() / 2) - 2; // leggero aggiustamento ottico
            g2d.drawString(text, textX, textY);
        }
    }
}