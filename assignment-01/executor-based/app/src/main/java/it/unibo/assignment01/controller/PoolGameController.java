package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.model.ball.BallImpl;
import it.unibo.assignment01.tasks.CollisionTask;
import it.unibo.assignment01.tasks.UpdateMovementTask;
import it.unibo.assignment01.util.Latch;
import it.unibo.assignment01.view.View;
import it.unibo.assignment01.view.ViewModel;

public class PoolGameController extends Thread implements Controller {

	private final View view;

	private final int NUM_WORKERS;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final Latch latch;
	private final Executor exec;
	private final SpatialHashGrid bigBallSpatialHashGrid;
	private final ViewModel vm;

	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;

	public PoolGameController(final View view) {
		this.view = view;
		this.NUM_WORKERS = Runtime.getRuntime().availableProcessors() - 1;

		this.board = new BoardImpl(createBalls(50, 90), new SimpleCollisionDetector());
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS*2, board.getBounds());
		this.bigBallSpatialHashGrid = new SpatialHashGrid(Ball.AGENT_BALL_RADIUS, board.getBounds());
		latch = new Latch(NUM_WORKERS);

		this.exec = Executors.newFixedThreadPool(NUM_WORKERS);
		vm = new ViewModel(board);
	}

	@Override
    public void run() {
		int nFrames = 0;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();
		while (!board.endedGame()){

			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();
			spatialHashGrid.clear();
			bigBallSpatialHashGrid.clear();

			processHeldKeys();

			for(int i = 0; i < NUM_WORKERS; i++) {
				exec.execute(new UpdateMovementTask(board.getAllBall(), elapsed, board, latch, i, NUM_WORKERS));
			}

			try {
				latch.await();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
			for (Ball ball : board.getBalls()) {
				spatialHashGrid.insert(ball);
				bigBallSpatialHashGrid.insert(ball);
			}

			for (int i = 0; i < NUM_WORKERS; i++) {
				exec.execute(new CollisionTask(board, latch, spatialHashGrid, i, NUM_WORKERS));
			}
			CollisionTask.resolveNearbyCollisions(board.getPlayerBall(), bigBallSpatialHashGrid, board);
			bigBallSpatialHashGrid.insert(board.getPlayerBall());
			CollisionTask.resolveNearbyCollisions(board.getEnemyBall(), bigBallSpatialHashGrid, board);
			latch.refresh();
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

			vm.update(board);
			view.update(vm, framePerSec);
			latch.refresh();
		}
		view.showEndGame(board.getWinner());

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
