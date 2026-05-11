package it.unibo.assignment01.controller;

import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;



public class CollisionTask implements Runnable{

    private Board board;
    private List<Ball> myBatch;
    private List<List<Ball>> allBatches;
    private int batchIndex;
    private Barrier barrier;
    private SpatialHashGrid grid;

    public CollisionTask(int batchIndex, List<Ball> myBatch, List<List<Ball>> allBatches, Board board, Barrier barrier, SpatialHashGrid grid){
        this.board = board;
        this.myBatch = myBatch;
        this.allBatches = allBatches;
        this.batchIndex = batchIndex;
        this.barrier = barrier;
        this.grid = grid;
    }


    @Override
    public void run() {

        for (Ball ball : myBatch) {

            int cx = grid.getCellX(ball);
            int cy = grid.getCellY(ball);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {

                    List<Ball> nearby =
                            grid.getCell(cx + dx, cy + dy);

                    for (Ball other : nearby) {

                        if (ball == other) {
                            continue;
                        }

                        board.resolveCollision(ball, other);
                    }
                }
            }
        }

        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* 
    @Override
    public void run() {
        // Check myBatch against all batches with index >= batchIndex
        for (int i = batchIndex; i < allBatches.size(); i++) {
            List<Ball> otherBatch = allBatches.get(i);
            
            if (i == batchIndex) {
                // Within the same batch, check unique pairs only
                for (int j = 0; j < myBatch.size(); j++) {
                    for (int k = j + 1; k < myBatch.size(); k++) {
                        board.resolveCollision(myBatch.get(j), myBatch.get(k));
                    }
                }
            } else {
                // Against other batches
                for (Ball b : myBatch) {
                    for (Ball a : otherBatch) {
                        board.resolveCollision(a, b);
                    }
                }
            }
        }
        
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

}
