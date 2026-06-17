package it.unibo.assignment01;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import it.unibo.assignment01.controller.Cmd;
import it.unibo.assignment01.controller.CollisionTask;
import it.unibo.assignment01.controller.Controller;
import it.unibo.assignment01.controller.SpatialHashGrid;
import it.unibo.assignment01.controller.UpdateMovementTask;
import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.BallImpl;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.util.BoundedBuffer;
import it.unibo.assignment01.util.BoundedBufferImpl;
import it.unibo.assignment01.util.Pair;
import it.unibo.assignment01.util.SynchCell;
import it.unibo.assignment01.worker.BallWorker;

public class ControllerJpf extends Thread implements Controller {

	private final int NUM_WORKERS = 2;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final BoundedBuffer<Runnable> queueTask;
	private final List<Pair<SynchCell<Runnable>, BallWorker>> workers;
	private CountDownLatch latch;

	public ControllerJpf() {

		this.board = new BoardImpl(createBalls(), new SimpleCollisionDetector());
		this.queueTask = new BoundedBufferImpl<>(NUM_WORKERS * 2);
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * 1.6);
		this.workers = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			SynchCell<Runnable> cell = new SynchCell<>();
			var worker = new BallWorker(cell);
			this.workers.add(new Pair<SynchCell<Runnable>, BallWorker>(cell, worker));
		}
	}

	@Override
	public void run() {
		for (int j = 0; j < 1; j++) {
			latch = new CountDownLatch(NUM_WORKERS);
			spatialHashGrid.clear();

			Thread t1 = new Thread(() -> {
					try {
						workers.get(0).getX().get().run();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			});
			t1.start();
			Thread t2 = new Thread(() -> {
					try {
						workers.get(1).getX().get().run();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			});
			t2.start();

			for(int i = 0; i< NUM_WORKERS; i++) {
				addWorkerTask(new UpdateMovementTask(board.getBalls(), 16, board, latch, i, NUM_WORKERS), this.workers.get(i).getX());
			}
			// By hitting the barrier the BallWorkers are release and can execute the task
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Ball ball : board.getAllBall()) {
				spatialHashGrid.insert(ball);
			}

			t1 = new Thread(() -> {
					try {
						workers.get(0).getX().get().run();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			});
			t1.start();
			t2 = new Thread(() -> {

					try {
						workers.get(1).getX().get().run();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			});
			t2.start();

			latch = new CountDownLatch(NUM_WORKERS);
			// Calculate collisions with pair-wise checking to eliminate redundancy
			List<Map.Entry<Long, List<Ball>>> cells = new ArrayList<>(spatialHashGrid.getCells());
			
			// Calculate collisions with pair-wise checking to eliminate redundancy
			for (int i = 0; i < NUM_WORKERS; i++) {
				addWorkerTask(new CollisionTask(cells, board, latch, spatialHashGrid, i, NUM_WORKERS), this.workers.get(i).getX());
			}
			// Maybe another hitAndWait()...
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	@Override
	public void notifyCommand(Cmd cmd) {
	}

}
