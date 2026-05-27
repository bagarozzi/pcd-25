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
    private final int startIndex;
    private final int endIndex;
    
    public UpdateMovementTask(List<Ball> ballBatch, int startIndex, int endIndex, long timeElapsed, Board board, CountDownLatch latch){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
        this.latch = latch;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            Ball ball = ballBatch.get(i);
            board.checkHole(ball);
            ball.updateState(timeElapsed, board);
        }

        latch.countDown();
    }
    
}