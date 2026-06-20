package jpftesting.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpftesting.util.Latch; 

import jpftesting.controller.CollisionTask;
import jpftesting.controller.SpatialHashGrid;
import jpftesting.controller.UpdateMovementTask;
import jpftesting.model.Ball;
import jpftesting.model.BallImpl;
import jpftesting.model.Board;
import jpftesting.model.BoardImpl;
import jpftesting.model.Position;
import jpftesting.model.SimpleCollisionDetector;
import jpftesting.model.Speed;
import jpftesting.util.Pair;
import jpftesting.util.SynchCell;
import jpftesting.worker.BallWorker;

public class ControllerJpf extends Thread {

	private final int NUM_WORKERS = 2;
	private final long STATIC_ELAPSED_TIME = 16L;

	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final List<Pair<SynchCell<Runnable>, BallWorker>> workers;
	private Latch latch;

	public ControllerJpf() {

		this.board = new BoardImpl(createBalls(), new SimpleCollisionDetector());
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * 1.6);
		this.workers = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			SynchCell<Runnable> cell = new SynchCell<>();
			var worker = new BallWorker(cell);
			this.workers.add(new Pair<SynchCell<Runnable>, BallWorker>(cell, worker));
			worker.start();
		}
	}

	@Override
	public void run() {
		for (int j = 0; j < 2; j++) {
			latch = new Latch(NUM_WORKERS);
			spatialHashGrid.clear();

			for(int i = 0; i< NUM_WORKERS; i++) {
				addWorkerTask(new UpdateMovementTask(board.getAllBall(), STATIC_ELAPSED_TIME, board, latch, i, NUM_WORKERS), this.workers.get(i).getX());
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

			for (Ball ball : board.getAllBall()) {
				spatialHashGrid.insert(ball);
			}

			latch.refresh();
			// Calculate collisions with pair-wise checking to eliminate redundancy
			List<Map.Entry<Long, List<Ball>>> cells = new ArrayList<>(spatialHashGrid.getCells());
			
			// Calculate collisions with pair-wise checking to eliminate redundancy
			for (int i = 0; i < NUM_WORKERS; i++) {
				addWorkerTask(new CollisionTask(cells, board, latch, spatialHashGrid, i, NUM_WORKERS), this.workers.get(i).getX());
			}
			// Maybe another hitAndWait()...
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			latch.refresh();
		}

		for(Pair<SynchCell<Runnable>, BallWorker> w : workers) {
            addWorkerTask(() -> Thread.currentThread().interrupt(), w.getX());
        }
	}

	private void addWorkerTask(Runnable task, SynchCell<Runnable> cell) {
		cell.set(task);
	}

	private <T> List<List<T>> splitList(List<T> list, int nList) {
		List<List<T>> res = new ArrayList<>();
		for (int i = 0; i < nList; i++) {
			int start = i * list.size() / nList;
			int end = (i + 1) * list.size() / nList;
			res.add(List.copyOf(list.subList(start, end)));
		}
		// System.err.println("Split list of size " + res.size() + nList );
		// res.stream().forEach(l -> System.err.println(l.size()));
		return res;
	}

	private List<Ball> createBalls() {
		var balls = new ArrayList<Ball>();
		balls.add(new BallImpl(new Position(0, 0), new Speed(0, 0), 0.2, Ball.BALL_RADIUS));
		balls.add(new BallImpl(new Position(Ball.BALL_RADIUS, 0), new Speed(-1.0, 0), 0.2, Ball.BALL_RADIUS));
		return balls;
	}

}
