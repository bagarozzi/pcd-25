package it.unibo.assignment01.tasks;

import java.util.List;

import it.unibo.assignment01.controller.Barrier;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.ball.Ball;

public class CheckHoleTask implements Runnable {
    private final List<Ball> ballBatch;
    private final Board board;
    private final Barrier barrier;
    
    public CheckHoleTask(List<Ball> ballBatch, Board board, Barrier barrier){
        this.ballBatch = ballBatch;
        this.board = board;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        System.out.println("Worker " + Thread.currentThread().getName() + " is working...");
        ballBatch.stream().forEach(ball -> board.checkHole(ball));
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
