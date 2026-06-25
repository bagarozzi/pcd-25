package it.unibo.assignment01.worker;

import java.util.List;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.util.Latch;

public class CheckHoleTask implements Runnable {
    private final List<Ball> ballBatch;
    private final Board board;
    private final Latch latch;
    
    public CheckHoleTask(List<Ball> ballBatch, Board board, Latch latch){
        this.ballBatch = ballBatch;
        this.board = board;
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Worker " + Thread.currentThread().getName() + " is working...");
        ballBatch.stream().forEach(ball -> board.checkHole(ball));
        latch.countDown();
    }
}
