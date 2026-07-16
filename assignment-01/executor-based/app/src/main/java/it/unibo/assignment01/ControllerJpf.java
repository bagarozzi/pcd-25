package it.unibo.assignment01;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.unibo.assignment01.controller.Cmd;
import it.unibo.assignment01.controller.Controller;
import it.unibo.assignment01.controller.SpatialHashGrid;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.model.ball.BallImpl;
import it.unibo.assignment01.tasks.CollisionTask;
import it.unibo.assignment01.tasks.UpdateMovementTask;


public class ControllerJpf extends Thread implements Controller {

	private final int NUM_WORKERS = 2;
	private Board board;
	private SpatialHashGrid spatialHashGrid;

	private final Executor exec;

	public ControllerJpf() {

		this.board = new BoardImpl(createBalls(), new SimpleCollisionDetector());
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS*2);

		this.exec = Executors.newFixedThreadPool(NUM_WORKERS);
	}

	@Override
    public void run() {
		long lastUpdateTime = System.currentTimeMillis();

		// For enemy player movement
		//var pb = board.getPlayerBall();

		for(int j = 0; j < 2; j++) {

			// Upgrade ball movements and collisions, knowing the last time the board was updated and the current time.
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();
			spatialHashGrid.clear();
            CountDownLatch moveLatch = new CountDownLatch(NUM_WORKERS);
            CountDownLatch collideLatch = new CountDownLatch(NUM_WORKERS);

			// Process continuous keyboard input
			//updateBallBatches();
			for(int i = 0; i < NUM_WORKERS; i++) {
				int start = i * board.getAllBall().size() / NUM_WORKERS;
				int end = (i + 1) * board.getAllBall().size() / NUM_WORKERS;
				exec.execute(new UpdateMovementTask(board.getAllBall(), start, end, elapsed, board, moveLatch));
			}


			// By hitting the barrier the BallWorkers are release and can execute the task
			try {
				moveLatch.await();
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
				exec.execute(new CollisionTask(ballBatches.get(i), board, collideLatch, spatialHashGrid));
			}

			// Maybe another hitAndWait()...
			try {
			 	collideLatch.await();
			} catch (InterruptedException e) {

			 	e.printStackTrace();
			}
			

		}


    }


	private <T> List<List<T>> splitList(List<T> list, int nList) {
		List<List<T>> res = new ArrayList<>();
		for (int i = 0; i < nList; i++) {
			int start = i * list.size() / nList;
			int end = (i + 1) * list.size() / nList;
			res.add(list.subList(start, end));
		}
		return res;
	}

	private List<Ball> createBalls() {
        var balls = new ArrayList<Ball>();
		balls.add(new BallImpl(new Position(0, 0), new Speed(0, 0), 0.2,  Ball.BALL_RADIUS));
        balls.add(new BallImpl(new Position(Ball.BALL_RADIUS, 0), new Speed(-1.0, 0), 0.2,  Ball.BALL_RADIUS));
        return balls;
	}

    @Override
    public void notifyCommand(Cmd cmd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyCommand'");
    }


}
