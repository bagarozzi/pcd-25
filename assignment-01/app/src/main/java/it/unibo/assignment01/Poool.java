package it.unibo.assignment01;

import it.unibo.assignment01.controller.PoolGameController;
import it.unibo.assignment01.view.View;
import it.unibo.assignment01.controller.Controller;

public class Poool {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    public static void main(String[] args) {
        Controller controller = new PoolGameController();
        View view = new View(FRAME_WIDTH, FRAME_HEIGHT);
        view.setController(controller);
        controller.start();
        view.display();
    }
}
