package it.unibo.assignment01.model;

import java.util.Random;

public class EnemyBall extends BallImpl {

    private long lastKickTime = 0;
    private static final long KICK_INTERVAL = 2000;
    private static final Random rand = new Random(2);

    public EnemyBall(Position pos, Speed vel, double mass, double radius) {
        super(pos, vel, mass, radius);
    }

    @Override
    public void updateState(long elapsed, Board ctx) {
        if (this.getVel().abs() < 0.05 && lastKickTime > KICK_INTERVAL) {
			var angle = rand.nextDouble()*Math.PI*0.25;
			var v = new Speed(Math.cos(angle),Math.sin(angle)).mul(1.5);
			this.setVel(v);
			lastKickTime = 0;
		} else {
            lastKickTime += elapsed;
        }
        super.updateState(elapsed, ctx);
    }
}
