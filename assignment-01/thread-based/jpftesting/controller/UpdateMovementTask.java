package jpftesting.controller;


import java.util.List;

import jpftesting.model.Ball;
import jpftesting.model.Board;
import jpftesting.util.Latch;

public class UpdateMovementTask implements Runnable{
    private List<Ball> ballBatch;
    private long timeElapsed;
    private final Board board;
    private final Latch latch;
    private int index;
    private int numWorker;
    
    public UpdateMovementTask(List<Ball> ballBatch, long timeElapsed, Board board, Latch latch, int workerIndex, int numWorker){
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
        this.board = board;
        this.latch = latch;
        this.index = workerIndex;
        this.numWorker = numWorker;
    }

    public void updateParamethers(final List<Ball> ballBatch, long timeElapsed) {
        this.ballBatch = ballBatch;
        this.timeElapsed = timeElapsed;
    }

    @Override
    public void run() {
        for(int i= this.index; i < ballBatch.size(); i += numWorker) {
            board.checkHole(ballBatch.get(i));
            ballBatch.get(i).updateState(timeElapsed, board);
        }
        latch.countDown();
    }
    
}