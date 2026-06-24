package jpftesting.model;

public interface Ball {

    public static final double BALL_RADIUS = 0.01;
    public static final double AGENT_BALL_RADIUS = 0.07;

    public void updateState(long dt, Board ctx);

    public Ball getSnapshot();

    public Position getPos();
    
    public double getMass();
    
    public Speed getVel();
    
    public double getRadius();

    public void setPos(Position pos);

    public void setVel(Speed vel);

    public boolean isColliding(Ball other);

    public  void kick(Speed impulse);
}
