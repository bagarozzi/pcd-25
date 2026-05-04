package it.unibo.assignment01.controller;

import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;


public class CollisionTask implements Runnable{

    private List<Ball> balls;
    private Barrier barrier;

    public CollisionTask(List<Ball> balls, Barrier barrier){
        this.balls = balls;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        balls.stream().forEach(b -> {
            balls.stream().filter(other -> other != b)
            .filter(other -> b.isColliding(other))
            .forEach(other -> Board.resolveCollision(other, b));
        });
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    

}
