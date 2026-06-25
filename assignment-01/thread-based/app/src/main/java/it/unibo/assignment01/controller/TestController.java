package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.List;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.TestBoard;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.model.ball.BallImpl;
import it.unibo.assignment01.util.Latch;
import it.unibo.assignment01.util.Pair;
import it.unibo.assignment01.util.SynchCell;
import it.unibo.assignment01.worker.BallWorker;
import it.unibo.assignment01.worker.CollisionTask;
import it.unibo.assignment01.worker.UpdateMovementTask;

public class TestController extends Thread {

	private static final double MULTIPY_FACTOR_FOR_RADIOUS = 1.6;
    private static final long STATIC_ELAPSED_FRAME_TIME = 16;

	private final int NUM_WORKERS;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final List<Pair<SynchCell<Runnable>, BallWorker>> workers;
	private final List<Pair<UpdateMovementTask, CollisionTask>> staticTasks;
    private final Latch latch;

    private final int testFrames;
    private final int warmupFrames;


	public TestController(int workersNum, int testFrames, int warmupFrames) {
		this.NUM_WORKERS = workersNum;
        this.testFrames = testFrames;
        this.warmupFrames = warmupFrames;

		this.board = new TestBoard(createBalls(50, 90), new SimpleCollisionDetector());
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * MULTIPY_FACTOR_FOR_RADIOUS, board.getBounds());

		this.workers = new ArrayList<>();
		this.latch = new Latch(NUM_WORKERS);
		this.staticTasks = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			SynchCell<Runnable> cell = new SynchCell<>();
			var worker = new BallWorker(cell);
			this.workers.add(new Pair<SynchCell<Runnable>, BallWorker>(cell, worker));
			staticTasks.add(
				new Pair<>(
					new UpdateMovementTask(board.getAllBall(), 0, board, latch, i, NUM_WORKERS),
					new CollisionTask(board, latch, spatialHashGrid, i, NUM_WORKERS)
			));
			worker.start();
		}
	}

	@Override
    public void run() {

        long testStartTime = 0;

		for(int k = 0; k < testFrames + warmupFrames; k++) {

            if(k == warmupFrames) {
                testStartTime = System.nanoTime();
            }

			spatialHashGrid.clear();

			for(int i = 0; i< NUM_WORKERS; i++) {
				staticTasks.get(i).getX().updateParamethers(board.getAllBall(), STATIC_ELAPSED_FRAME_TIME);
				addWorkerTask(staticTasks.get(i).getX(), this.workers.get(i).getX());
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Ball ball : board.getBalls()) {
				spatialHashGrid.insert(ball);
			}

			latch.refresh();

			for (int i = 0; i < NUM_WORKERS; i++) {
				addWorkerTask(staticTasks.get(i).getY(), this.workers.get(i).getX());
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            latch.refresh();
		}
        var endTime = System.nanoTime();
        long testTime = (endTime - testStartTime)/1_000_000;
        System.out.println("Test with " + NUM_WORKERS + " workers took " + testTime + " millis");
		for(Pair<SynchCell<Runnable>, BallWorker> w : workers) {
            addWorkerTask(() -> Thread.currentThread().interrupt(), w.getX());
        }
    }

	private void addWorkerTask(Runnable task, SynchCell<Runnable> cell) {
		cell.set(task);
	}

	private List<Ball> createBalls(final int rows, final int cols) {

        var balls = new ArrayList<Ball>();

		double startX = -((cols / 2.0) * (0.01 + Ball.BALL_RADIUS));
		double startY = Math.max(-0.25, -(rows / 3.0) * (0.01 + Ball.BALL_RADIUS));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                var px = startX + j * (0.01 + Ball.BALL_RADIUS);
                var py = startY + i * (0.01 + Ball.BALL_RADIUS);
                var b = new BallImpl(new Position(px, py), new Speed(0,0), 0.2, Ball.BALL_RADIUS);
                balls.add(b);
				if (balls.size() >= rows*cols) {
					break;
				}
            }
        }
        return balls;
	}


}
