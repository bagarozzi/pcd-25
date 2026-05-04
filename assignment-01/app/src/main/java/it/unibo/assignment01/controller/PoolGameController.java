package it.unibo.assignment01.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.util.BoundedBuffer;
import it.unibo.assignment01.util.BoundedBufferImpl;
import it.unibo.assignment01.worker.BallWorker;

public class PoolGameController extends Thread implements Controller {
	private Barrier workersBarrier;
	private final int NUM_WORKERS = Runtime.getRuntime().availableProcessors() + 1;
	private Board board;

	private final int N_WORKERS;

	private final BoundedBuffer<Cmd> cmdBuffer;
	private final BoundedBuffer<Runnable> queueTask;
	private final Barrier barrier;
	private final List<BallWorker> workers;

	public PoolGameController() {
		this.N_WORKERS = Runtime.getRuntime().availableProcessors();

		this.board = new BoardImpl();
		this.queueTask = new BoundedBufferImpl<>(10);
		cmdBuffer = new BoundedBufferImpl<>(10);
		this.barrier = new Barrier(N_WORKERS + 1);
		this.workers = new ArrayList<>();
		for (int i = 0; i < N_WORKERS; i++) {
			this.workers.add(new BallWorker(queueTask, null, barrier));
		}
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'start'");
	}

	@Override
    public void run() {
		int nFrames = 0;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();

		// For enemy player movement
		//var pb = board.getPlayerBall();
		var rand = new Random(2);
		var lastKickTime = t0;
		
		while (true){
		
			// Upgrade ball movements and collisions, knowing the last time the board was updated and the current time.
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();
	
			splitList(board.getBalls(), NUM_WORKERS).stream().
			forEach((ballBatch) -> addWorkerTask(new UpdateMovementTask(ballBatch, elapsed, board, workersBarrier)));

			// By hitting the barrier the BallWorkers are release and can execute the task
			try {
				barrier.hitAndWait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			// Calculate collisions...

			// Maybe another hitAndWait()...
			
			nFrames++;
			int framePerSec = 0;
			long dt = (System.currentTimeMillis() - t0);
			if (dt > 0) {
				framePerSec = (int)(nFrames*1000/dt);
			}

            // Render the view after calculating how many frames have passed during the calculation
			// view.update()
			// view.render()
			// VCBarrier.hitAndWait()
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
			res.add(list.subList(start, end));
		}
		return res;
	}

	
}
