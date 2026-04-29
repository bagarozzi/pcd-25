package it.unibo.assignment01.model;

public interface Ball {

    public void updateState(long dt, Board ctx);

    public void kick(Speed vel);

    public Position getPos();
    
    public double getMass();
    
    public Speed getVel();
    
    public double getRadius();
}
