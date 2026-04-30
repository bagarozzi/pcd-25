package it.unibo.assignment01.controller;


import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class UpdateMovementTask implements Runnable{
    List<Ball> ballBatch;
    long timeElapsed;
    Board board;
    
    public UpdateMovementTask(List<Ball> ballBatch, long timeElapsed, Board board){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
    }
    @Override
    public void run() {
        System.out.println("Worker " + Thread.currentThread().getName() + " is working...");
        ballBatch.stream().forEach(ball -> ball.updateState(timeElapsed, board));
    }
    
}