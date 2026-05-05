package it.unibo.assignment01.model;

import java.util.List;

public interface CollisionDetector {

    public List<CollisionPair> detectCollisions(List<Ball> balls);

    public void resolveCollision(CollisionPair collision);

}
