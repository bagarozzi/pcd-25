package it.unibo.assignment01.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;

public class CollisionTask implements Runnable {

    private Board board;
    private List<Map.Entry<Long, List<Ball>>> myBatch;
    private CountDownLatch latch;
    private SpatialHashGrid grid;
    private int index;
    private int numWorker;

    public CollisionTask(List<Map.Entry<Long, List<Ball>>> myBatch, Board board, CountDownLatch latch,
            SpatialHashGrid grid, int workIndex, int numWorker) {
        this.board = board;
        this.myBatch = myBatch;
        this.latch = latch;
        this.grid = grid;
        this.index = workIndex;
        this.numWorker = numWorker;
    }

    @Override
    public void run() {

        for (int i = this.index; i < myBatch.size(); i += this.numWorker) {
            for (Ball ball : myBatch.get(i).getValue()) {
                resolveNearbyCollisions(ball, grid, board);
            }
        }
        latch.countDown();
    }

    public static void resolveNearbyCollisions(Ball ball, SpatialHashGrid grid, Board board) {
        int cx = grid.getCellX(ball);
        int cy = grid.getCellY(ball);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                List<Ball> nearby = grid.getCell(cx + dx, cy + dy);

                for (Ball other : nearby) {

                    if (!ball.equals(other)) {
                        board.resolveCollision(ball, other);
                        ;
                    }
                }
            }
        }
    }

    /*
     * @Override
     * public void run() {
     * // Check myBatch against all batches with index >= batchIndex
     * for (int i = batchIndex; i < allBatches.size(); i++) {
     * List<Ball> otherBatch = allBatches.get(i);
     * 
     * if (i == batchIndex) {
     * // Within the same batch, check unique pairs only
     * for (int j = 0; j < myBatch.size(); j++) {
     * for (int k = j + 1; k < myBatch.size(); k++) {
     * board.resolveCollision(myBatch.get(j), myBatch.get(k));
     * }
     * }
     * } else {
     * // Against other batches
     * for (Ball b : myBatch) {
     * for (Ball a : otherBatch) {
     * board.resolveCollision(a, b);
     * }
     * }
     * }
     * }
     * 
     * try {
     * barrier.hitAndWait();
     * } catch (InterruptedException e) {
     * e.printStackTrace();
     * }
     * }
     */

}
