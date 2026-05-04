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
        System.out.println("Worker " + Thread.currentThread().getName() + " is working...");
        ballBatch.stream().forEach(ball -> ball.updateState(timeElapsed, board));
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}