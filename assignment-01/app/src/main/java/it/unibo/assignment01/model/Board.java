package it.unibo.assignment01.model;

import java.util.List;

public interface Board {

    public List<Ball> getBalls();
    
    public Ball getPlayerBall();
    
    public  Boundary getBounds();
    
}
