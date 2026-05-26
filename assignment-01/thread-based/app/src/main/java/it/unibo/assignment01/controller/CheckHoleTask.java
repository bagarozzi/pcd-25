package it.unibo.assignment01.controller;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class CheckHoleTask implements Runnable {
    private final List<Ball> ballBatch;
    private final Board board;
    private final CountDownLatch latch;
    
    public CheckHoleTask(List<Ball> ballBatch, Board board, CountDownLatch latch){
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
