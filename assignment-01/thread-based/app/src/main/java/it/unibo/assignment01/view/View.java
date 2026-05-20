package it.unibo.assignment01.view;

import it.unibo.assignment01.controller.Controller;
import it.unibo.assignment01.controller.MoveCmd;
import it.unibo.assignment01.controller.PoolGameController;

import javax.swing.SwingUtilities;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class View {

    private final ViewFrame frame;
    private Controller controller;

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

    public void setController(Controller controller) {
        this.controller = controller;
        this.frame.setController(controller);
    }

    public boolean[] getPressedKeys() {
        return frame.getPanel().getPressedKeys();
    }

    public void showEndGame(String winner) {
        frame.showEndGame(winner);
    }

}