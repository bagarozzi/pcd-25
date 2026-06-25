package it.unibo.assignment01.worker;

import java.util.List;
import java.util.Map;

import it.unibo.assignment01.controller.SpatialHashGrid;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.util.Latch;

public class CollisionTask implements Runnable {

    private Board board;
    private Latch latch;
    private SpatialHashGrid grid;
    private int index;
    private int numWorker;

    private static final int[][] NEIGHBOR_OFFSETS = {
        {1, 0},   // Right (East)
        {-1, 1},  // Bottom-Left (South-West)
        {0, 1},   // Bottom (South)
        {1, 1}    // Bottom-Right (South-East)
    };

    public CollisionTask(Board board, Latch latch,
            SpatialHashGrid grid, int workIndex, int numWorker) {
        this.board = board;
        this.latch = latch;
        this.grid = grid;
        this.index = workIndex;
        this.numWorker = numWorker;
    }

    @Override
    public void run() {

        for (int y = this.index; y < grid.getRows(); y += this.numWorker) {
            for(int x = 0; x < grid.getCols(); x++) {
                List<Ball> cellBalls = grid.getCell(x, y);
                if(cellBalls.isEmpty()) continue;
                for (int i = 0; i < cellBalls.size(); i++) {
                    Ball b1 = cellBalls.get(i);
                    for (int j = i + 1; j < cellBalls.size(); j++) {
                        board.resolveCollision(b1, cellBalls.get(j));
                    }
                }
                for (int[] offset : NEIGHBOR_OFFSETS) {
                    checkNeighborCell(cellBalls, grid.getCell(x + offset[0], y + offset[1]));
                }
            }
        }
        latch.countDown();
    }

    private void checkNeighborCell(List<Ball> myCell, List<Ball> neighborCell) {
        if (neighborCell == null || neighborCell.isEmpty()) return;
        for (Ball b1 : myCell) {
            for (Ball b2 : neighborCell) {
                board.resolveCollision(b1, b2);
            }
        }
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
