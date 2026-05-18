package it.unibo.assignment01.controller;


import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class UpdateMovementTask implements Runnable{
    private final List<Ball> ballBatch;
    private final long timeElapsed;
    private final Board board;
    private final Barrier barrier;
    private final int startIndex;
    private final int endIndex;
    
    public UpdateMovementTask(List<Ball> ballBatch, int startIndex, int endIndex, long timeElapsed, Board board, Barrier barrier){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
        this.barrier = barrier;
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

        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}