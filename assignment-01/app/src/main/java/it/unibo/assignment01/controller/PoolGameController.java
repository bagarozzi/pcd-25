package it.unibo.assignment01.controller;

import java.util.Random;

import it.unibo.assignment01.model.Speed;

public class PoolGameController extends Thread implements Controller {

    public PoolGameController() {

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
			
			/* if the player ball is stopped and 5 secs have elapsed, then kick the player ball */

			/*if (pb.getVel().abs() < 0.05 && System.currentTimeMillis() - lastKickTime > 2000) {
				var angle = rand.nextDouble()*Math.PI*0.25;
				var v = new Speed(Math.cos(angle),Math.sin(angle)).mul(1.5);
				pb.kick(v);
				lastKickTime = System.currentTimeMillis();
			}*/
		
			// Upgrade ball movements and collisions, knowing the last time the board was updated and the current time.
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();			

			
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

    
}