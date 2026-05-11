package it.unibo.assignment01.controller;


import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class UpdateMovementTask implements Runnable{
    private final List<Ball> ballBatch;
    private final long timeElapsed;
    private final Board board;
    private final Barrier barrier;
    
    public UpdateMovementTask(List<Ball> ballBatch, long timeElapsed, Board board, Barrier barrier){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        for (Ball ball : ballBatch) {
            ball.updateState(timeElapsed, board);
        }

        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}