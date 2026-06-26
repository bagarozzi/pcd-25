package jpftesting.model;

import jpftesting.model.Position;
import jpftesting.model.Speed;

public class BallView {

    private Position pos;
    private Speed vel;
    private final double mass;
    private final double radius;

    public BallView(Position pos, Speed vel, double mass, double radius) {
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
        this.radius = radius;
    }

    public Position getPos() {
        return this.pos;
    }

    public double getMass() {
        return this.mass;
    }

    public Speed getVel() {
        return this.vel;
    }

    public double getRadius() {
        return this.radius;
    }

}
