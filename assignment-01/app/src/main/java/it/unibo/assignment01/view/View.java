package it.unibo.assignment01.view;

import it.unibo.assignment01.controller.Controller;
import it.unibo.assignment01.controller.MoveCmd;

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
        this.frame.requestFocusInWindow();

        // Ascoltatore per i comandi di movimento
        this.frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (controller != null) {
                    handleKeyPress(e.getKeyCode());
                }
            }
        });
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // Mostra la finestra nell'Event Dispatch Thread (Thread UI)
    public void display() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    // Aggiorna il rendering in modo thread-safe
    public void update(ViewModel viewModel, final long frameNumber) {
        SwingUtilities.invokeLater(() -> frame.updateView(viewModel, frameNumber));
    }

    // Mappa la pressione dei tasti ai comandi per il controller
    private void handleKeyPress(int keyCode) {

        switch (keyCode) {
            case KeyEvent.VK_UP:
                controller.notifyCommand(new MoveCmd(0, -1));
                break;
            case KeyEvent.VK_DOWN:
                controller.notifyCommand(new MoveCmd(0, 1));
                break;
            case KeyEvent.VK_LEFT:
                controller.notifyCommand(new MoveCmd(-1, 0));
                break;
            case KeyEvent.VK_RIGHT:
                controller.notifyCommand(new MoveCmd(1, 0));
                break;
        }
    }
}