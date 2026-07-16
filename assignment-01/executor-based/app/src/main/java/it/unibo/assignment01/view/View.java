package it.unibo.assignment01.view;
import javax.swing.SwingUtilities;
public class View {

    private final ViewFrame frame;

    public View(int width, int height) {
        this.frame = new ViewFrame(width, height);

        // Imposta il focus per catturare gli eventi della tastiera
        this.frame.setFocusable(true);
    }

    // Mostra la finestra nell'Event Dispatch Thread (Thread UI)
    public void display() {
       frame.setVisible(true);
       // Request focus on the panel after the window is visible
       SwingUtilities.invokeLater(() -> frame.getPanel().requestFocusInWindow());
    }

    // Aggiorna il rendering in modo thread-safe
    public void update(ViewModel viewModel, final long frameNumber) {
        frame.updateView(viewModel, frameNumber);
    }

    public boolean[] getPressedKeys() {
        return frame.getPanel().getPressedKeys();
    }

    public void showEndGame(String winner) {
        frame.showEndGame(winner);
    }
}