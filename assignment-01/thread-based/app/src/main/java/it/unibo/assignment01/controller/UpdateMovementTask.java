package it.unibo.assignment01.controller;


import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class UpdateMovementTask implements Runnable{
    private final List<Ball> ballBatch;
    private final long timeElapsed;
    private final Board board;
    private final CountDownLatch latch;
    
    public UpdateMovementTask(List<Ball> ballBatch, long timeElapsed, Board board, CountDownLatch latch){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (Ball ball : ballBatch) {
            board.checkHole(ball);
            ball.updateState(timeElapsed, board);
        }
        latch.countDown();
    }
    
}