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
import it.unibo.assignment01.worker.BallWorker;

public class ControllerJpf extends Thread implements Controller {

	private final int NUM_WORKERS = 2;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final BoundedBuffer<Cmd> cmdBuffer;
	private final BoundedBuffer<Runnable> queueTask;
	private final List<BallWorker> workers;
	private CountDownLatch latch;

	public ControllerJpf() {

		this.board = new BoardImpl(createBalls(), new SimpleCollisionDetector());
		this.queueTask = new BoundedBufferImpl<>(NUM_WORKERS * 2);
		cmdBuffer = new BoundedBufferImpl<>(10);
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * 1.6);
		this.workers = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			var worker = new BallWorker(queueTask);
			this.workers.add(worker);
			worker.start();
		}
	}

	@Override
    public void run() {


		for(int j=0; j<1; j++){
			latch = new CountDownLatch(NUM_WORKERS);
			spatialHashGrid.clear();

			splitList(board.getAllBall(), NUM_WORKERS).forEach((ballBatch) -> addWorkerTask(new UpdateMovementTask(ballBatch, 16, board, latch)));


			// By hitting the barrier the BallWorkers are release and can execute the task
			try {
				latch.await();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
			for (Ball ball : board.getAllBall()) {
				spatialHashGrid.insert(ball);
			}

			List<Map.Entry<Long, List<Ball>>> cells = new ArrayList<>(spatialHashGrid.getCells());
			
			// Calculate collisions with pair-wise checking to eliminate redundancy
			List<List<Map.Entry<Long,List<Ball>>>> ballBatches = splitList(cells, NUM_WORKERS);
			for (int i = 0; i < ballBatches.size(); i++) {
				addWorkerTask(new CollisionTask(ballBatches.get(i), board, latch, spatialHashGrid));
			}
			// Maybe another hitAndWait()...
			try {
			 	latch.await();
			} catch (InterruptedException e) {

			 	e.printStackTrace();
			}
		}
        for(BallWorker w : workers) {
            addWorkerTask(() -> Thread.currentThread().interrupt());
        }

    }

	public void notifyCommand(Cmd cmd) {
		try {
			cmdBuffer.put(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addWorkerTask(Runnable task) {
		try {
			queueTask.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private <T> List<List<T>> splitList(List<T> list, int nList) {
		List<List<T>> res = new ArrayList<>();
		for (int i = 0; i < nList; i++) {
			int start = i * list.size() / nList;
			int end = (i + 1) * list.size() / nList;
			res.add(List.copyOf(list.subList(start, end)));
		}
		//System.err.println("Split list of size " + res.size() + nList );
		//res.stream().forEach(l -> System.err.println(l.size()));
		return res;
	}

	private List<Ball> createBalls() {
        var balls = new ArrayList<Ball>();
		balls.add(new BallImpl(new Position(0, 0), new Speed(0, 0), 0.2,  Ball.BALL_RADIUS));
        balls.add(new BallImpl(new Position(Ball.BALL_RADIUS, 0), new Speed(-1.0, 0), 0.2,  Ball.BALL_RADIUS));
        return balls;
	}


}
