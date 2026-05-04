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
     	applyBoundaryConstraints(ctx);
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

    @Override
    public void setPos(Position pos) {
        this.pos = pos;
    }

    @Override
    public void setVel(Speed vel) {
        this.vel = vel;
    }

        /**
     * 
     * Keep the ball inside the boundaries, updating the velocity in the case of bounces
     * 
     * @param ctx
     */
    private void applyBoundaryConstraints(Board ctx){
        Boundary bounds = ctx.getBounds();
        if (this.getPos().x() + radius > bounds.x1()){
            setPos(new Position(bounds.x1() - radius, this.getPos().y()));
            vel = vel.getSwappedX();
        } else if (this.getPos().x() - radius < bounds.x0()){
            setPos(new Position(bounds.x0() + radius, this.getPos().y()));
            vel = vel.getSwappedX();
        } else if (this.getPos().y() + radius > bounds.y1()){
            setPos(new Position(this.getPos().x(), bounds.y1() - radius));
            vel = vel.getSwappedY();
        } else if (this.getPos().y() - radius < bounds.y0()){
            this.setPos(new Position(this.getPos().x(), bounds.y0() + radius));
            vel = vel.getSwappedY();
        }
    }

    public boolean isColliding(Ball other) {
        double dx   = other.getPos().x() - this.getPos().x();
        double dy   = other.getPos().y() - this.getPos().y();
        double dist = Math.hypot(dx, dy);
        double minD = this.getRadius() + other.getRadius();
        return dist < minD;
    }
    
}
