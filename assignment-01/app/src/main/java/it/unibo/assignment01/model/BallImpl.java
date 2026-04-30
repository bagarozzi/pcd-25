package it.unibo.assignment01.model;

public class BallImpl implements Ball {

    private static final double FRICTION_FACTOR = 0.1;

    private Position pos;
    private Speed vel;
    private final double mass;
    private final double radius;

    public BallImpl(Position pos, Speed vel, double mass, double radius) {
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
        this.radius = radius;
    }

    @Override
    public void updateState(long elapsed, Board ctx) {
        double speed = vel.abs();
        double dt_scaled = elapsed*0.001;
    	if (speed > 0.001) {
            double dec    = FRICTION_FACTOR * dt_scaled;
            double factor = Math.max(0, speed - dec) / speed;
            vel = vel.mul(factor);
        } else {
        	vel = new Speed(0,0);
        }
        pos = pos.sum(vel.mul(dt_scaled));
     	//applyBoundaryConstraints(ctx);
    }

    @Override
    public void kick(Speed vel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'kick'");
    }

    @Override
    public Position getPos() {
        return this.pos;
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public Speed getVel() {
        return this.vel;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }
    
}
