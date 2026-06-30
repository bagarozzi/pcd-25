package it.unibo.assignment01;

import it.unibo.assignment01.controller.TestController;

public class TestPoool {

    public static void main(String[] args) {
        //for(int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
            TestController controller = new TestController(6, 5000, 5000);
            controller.start();
            try {
                controller.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("Error occuored during tests: " + e);
                System.exit(1);
            }
        //}
        System.exit(0);
    }
    
}
