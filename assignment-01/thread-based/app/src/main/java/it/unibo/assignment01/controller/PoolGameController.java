package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.BallImpl;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.util.BoundedBuffer;
import it.unibo.assignment01.util.BoundedBufferImpl;
import it.unibo.assignment01.util.Latch;
import it.unibo.assignment01.util.Pair;
import it.unibo.assignment01.util.SynchCell;
import it.unibo.assignment01.view.View;
import it.unibo.assignment01.view.ViewModel;
import it.unibo.assignment01.worker.BallWorker;

public class PoolGameController extends Thread implements Controller {

	private static final double MULTIPY_FACTOR_FOR_RADIOUS = 1.6;
	private final View view;

	private final int NUM_WORKERS;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final BoundedBuffer<Cmd> cmdBuffer;
	private final List<Pair<SynchCell<Runnable>, BallWorker>> workers;
	private final SpatialHashGrid bigBallSpatialHashGrid;
	private final ViewModel vm;

	// Key indices for boolean array
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;

	public PoolGameController(final View view, final Barrier VCBarrier) {
		this.view = view;
		this.NUM_WORKERS = Runtime.getRuntime().availableProcessors();

		this.board = new BoardImpl(createBalls(50, 90), new SimpleCollisionDetector());
		cmdBuffer = new BoundedBufferImpl<>(10);
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * MULTIPY_FACTOR_FOR_RADIOUS, board.getBounds());
		this.bigBallSpatialHashGrid = new SpatialHashGrid(Ball.AGENT_BALL_RADIUS * 1.6, board.getBounds());
		this.workers = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			SynchCell<Runnable> cell = new SynchCell<>();
			var worker = new BallWorker(cell);
			this.workers.add(new Pair<SynchCell<Runnable>, BallWorker>(cell, worker));
			worker.start();
		}
		vm = new ViewModel(board);
	}

	@Override
    public void run() {
		int nFrames = 0;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();
		// For enemy player movement
		//var pb = board.getPlayerBall();

		while(!board.endedGame()){

			// Upgrade ball movements and collisions, knowing the last time the board was updated and the current time.
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();
			spatialHashGrid.clear();
			bigBallSpatialHashGrid.clear();
			Latch latch = new Latch(NUM_WORKERS);

			// Process continuous keyboard input
			processHeldKeys();

			cmdBuffer.lazyGet().ifPresent(cmd -> cmd.execute(board.getPlayerBall()));

			for(int i = 0; i< NUM_WORKERS; i++) {
				addWorkerTask(new UpdateMovementTask(board.getAllBall(), elapsed, board, latch, i, NUM_WORKERS), this.workers.get(i).getX());
			}


			// By hitting the barrier the BallWorkers are release and can execute the task
			// try {
			// 	moveBarrier.hitAndWait();
			// } catch (InterruptedException e) {

			// 	e.printStackTrace();
			// }
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Ball ball : board.getBalls()) {
				spatialHashGrid.insert(ball);
				bigBallSpatialHashGrid.insert(ball);
			}

			latch.refresh();
			
			// Calculate collisions with pair-wise checking to eliminate redundancy
			for (int i = 0; i < NUM_WORKERS; i++) {
				addWorkerTask(new CollisionTask(board, latch, spatialHashGrid, i, NUM_WORKERS), this.workers.get(i).getX());
			}
			CollisionTask.resolveNearbyCollisions(board.getPlayerBall(), bigBallSpatialHashGrid, board);
			bigBallSpatialHashGrid.insert(board.getPlayerBall());
			CollisionTask.resolveNearbyCollisions(board.getEnemyBall(), bigBallSpatialHashGrid, board);
			// Maybe another hitAndWait()...
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			


			nFrames++;
			int framePerSec = 0;
			long dt = (System.currentTimeMillis() - t0);
			if (dt > 0) {
				framePerSec = (int)(nFrames*1000/dt);
			}

            // Render the view after calculating how many frames have passed during the calculation
			vm.update(board);
			view.update(vm, framePerSec);
			/*try {
				VCBarrier.hitAndWait();
			} catch (InterruptedException e) {
				 
				e.printStackTrace();
			}*/
		}
		for(Pair<SynchCell<Runnable>, BallWorker> w : workers) {
            addWorkerTask(() -> Thread.currentThread().interrupt(), w.getX());
        }
		view.showEndGame(board.getWinner());
		
    }

	public void notifyCommand(Cmd cmd) {
		try {
			cmdBuffer.put(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void processHeldKeys() {
		boolean[] keys = view.getPressedKeys();
		if (keys[UP]) {
			board.getPlayerBall().kick(new Speed(0, 0.4));
		}
		if (keys[DOWN]) {
			board.getPlayerBall().kick(new Speed(0, -0.4));
		}
		if (keys[LEFT]) {
			board.getPlayerBall().kick(new Speed(-0.4, 0));
		}
		if (keys[RIGHT]) {
			board.getPlayerBall().kick(new Speed(0.4, 0));
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
