package it.unibo.assignment01.worker;


import java.util.List;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.util.Latch;

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
            if (board.checkHole(ballBatch.get(i))) continue;
            else if (i < ballBatch.size()) ballBatch.get(i).updateState(timeElapsed, board);
        }
        latch.countDown();
    }
    
}