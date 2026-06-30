package it.unibo.assignment01;

import it.unibo.assignment01.controller.PoolGameController;
import it.unibo.assignment01.util.Barrier;
import it.unibo.assignment01.view.View;

public class Poool {

    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 800;
    public static void main(String[] args) {
        Barrier VCBarrier = new Barrier(2);
        View view = new View(FRAME_WIDTH, FRAME_HEIGHT);
        PoolGameController controller = new PoolGameController(view, VCBarrier);
        view.setController(controller);
        controller.start();
        view.display();
    }
}
