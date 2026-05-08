package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.BallImpl;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.util.BoundedBuffer;
import it.unibo.assignment01.util.BoundedBufferImpl;
import it.unibo.assignment01.view.View;
import it.unibo.assignment01.view.ViewModel;
import it.unibo.assignment01.worker.BallWorker;
import java.awt.event.KeyEvent;

public class PoolGameController extends Thread implements Controller {

	private final View view;
	private final Barrier VCBarrier;

	private Barrier workersBarrier;
	private final int NUM_WORKERS;
	private Board board;

	private final BoundedBuffer<Cmd> cmdBuffer;
	private final BoundedBuffer<Runnable> queueTask;
	private final List<BallWorker> workers;
	
	// Key indices for boolean array
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;

	public PoolGameController(final View view, final Barrier VCBarrier) {
		this.view = view;
		this.VCBarrier = VCBarrier;
		this.NUM_WORKERS = Runtime.getRuntime().availableProcessors();

		this.board = new BoardImpl(createBalls(1,1), new SimpleCollisionDetector());
		this.queueTask = new BoundedBufferImpl<>(NUM_WORKERS * 2);
		cmdBuffer = new BoundedBufferImpl<>(10);
		this.workersBarrier = new Barrier(NUM_WORKERS + 1);
		this.workers = new ArrayList<>();
		for (int i = 0; i < NUM_WORKERS; i++) {
			var worker = new BallWorker(queueTask);
			this.workers.add(worker);
			worker.start();
		}
	}

	@Override
    public void run() {
		int nFrames = 0;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();

		// For enemy player movement
		//var pb = board.getPlayerBall();
		
		while (true){
		
			// Upgrade ball movements and collisions, knowing the last time the board was updated and the current time.
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();
			
			// Process continuous keyboard input
			processHeldKeys();
			
			cmdBuffer.lazyGet().ifPresent(cmd -> cmd.execute(board.getPlayerBall()));

			splitList(board.getBalls().stream().filter(b -> b.getVel().abs() > 0.001).toList(), NUM_WORKERS).forEach((ballBatch) -> addWorkerTask(new UpdateMovementTask(ballBatch, elapsed, board, workersBarrier)));
			board.getPlayerBall().updateState(elapsed, board);

			// By hitting the barrier the BallWorkers are release and can execute the task
			try {
				workersBarrier.hitAndWait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			// Calculate collisions...
			board.detectCollisions().forEach(p -> board.resolveCollision(p));
			//splitList(board.detectCollisions(), NUM_WORKERS).stream()
			//	.forEach(collisionBatch -> addWorkerTask(new CollisionTask(collisionBatch, board, workersBarrier)));
			// Maybe another hitAndWait()...
			nFrames++;
			int framePerSec = 0;
			long dt = (System.currentTimeMillis() - t0);
			if (dt > 0) {
				framePerSec = (int)(nFrames*1000/dt);
			}

            // Render the view after calculating how many frames have passed during the calculation
			ViewModel vm = new ViewModel(board);
			view.update(vm, framePerSec);
			/*try {
				VCBarrier.hitAndWait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

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
			board.getPlayerBall().setVel(new Speed(0, 0.4));
		}
		if (keys[DOWN]) {
			board.getPlayerBall().setVel(new Speed(0, -0.4));
		}
		if (keys[LEFT]) {
			board.getPlayerBall().setVel(new Speed(-0.4, 0));
		}
		if (keys[RIGHT]) {
			board.getPlayerBall().setVel(new Speed(0.4, 0));
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
		return res;
	}

	private List<Ball> createBalls(final int rows, final int cols) {

        var balls = new ArrayList<Ball>();

		double startX = -((cols / 2.0) * (0.01 + Ball.BALL_RADIUS));
		double startY = Math.max(-0.25, -(rows / 3.0) * (0.01 + Ball.BALL_RADIUS));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                var px = startX + j * (0.01 + Ball.BALL_RADIUS);
                var py = startY + i * (0.01 + Ball.BALL_RADIUS);
                var b = new BallImpl(new Position(px, py), new Speed(0,0), 1.0, Ball.BALL_RADIUS);
                balls.add(b);
				if (balls.size() >= rows*cols) {
					break;
				}
            }
        }
        return balls;
	}

	
}
