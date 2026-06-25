package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.BallImpl;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.TestBoard;
import it.unibo.assignment01.util.Latch;

public class SequentialController extends Thread {

	private static final double MULTIPY_FACTOR_FOR_RADIOUS = 1.6;
    private static final long STATIC_ELAPSED_FRAME_TIME = 16;

	private Board board;
	private SpatialHashGrid spatialHashGrid;
	private final Ball breakerBall;

    private final UpdateMovementTask movementTask;
    private final CollisionTask collisionTask;
    private final Latch latch;


    private final int testFrames;
    private final int warmupFrames;


	public SequentialController(int testFrames, int warmupFrames) {
        this.testFrames = testFrames;
        this.warmupFrames = warmupFrames;

        latch = new Latch(1);

		this.breakerBall = new BallImpl(new Position(-0.95, -0.35), new Speed(0,0), 15.0, Ball.BALL_RADIUS);
		List<Ball> balls = createBalls(50, 90);
		balls.add(breakerBall);
		this.board = new TestBoard(createBalls(50, 90), new SimpleCollisionDetector());
		this.spatialHashGrid = new SpatialHashGrid(Ball.BALL_RADIUS * MULTIPY_FACTOR_FOR_RADIOUS, board.getBounds());

        movementTask = new UpdateMovementTask(balls, STATIC_ELAPSED_FRAME_TIME, board, latch, 0, 1);
        collisionTask = new CollisionTask(board, latch, spatialHashGrid, 0, 1);

	}

	@Override
    public void run() {

        long testStartTime = 0;

		for(int k = 0; k < testFrames + warmupFrames; k++) {

            if(k == warmupFrames) {
                testStartTime = System.nanoTime();
				breakerBall.kick(new Speed(4,4));
            }

			spatialHashGrid.clear();

            movementTask.updateParamethers(board.getAllBall(), STATIC_ELAPSED_FRAME_TIME);
            movementTask.run();

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (Ball ball : board.getBalls()) {
				spatialHashGrid.insert(ball);
			}

			latch.refresh();

            collisionTask.run();

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            latch.refresh();
		}
        var endTime = System.nanoTime();
        long testTime = (endTime - testStartTime)/1_000_000;
        System.out.println("Sequential test took " + testTime + " millis");
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
