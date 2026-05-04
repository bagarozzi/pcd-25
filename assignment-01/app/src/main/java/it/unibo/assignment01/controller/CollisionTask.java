package it.unibo.assignment01.controller;

import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;


public class CollisionTask implements Runnable{

    private Board board;
    private List<Ball> balls;
    private Barrier barrier;

    public CollisionTask(List<Ball> balls,Board board, Barrier barrier){
        this.board = board;
        this.balls = balls;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        balls.stream().peek(b -> {
            balls.stream().filter(other -> other != b)
            .filter(other -> b.isColliding(other))
            .peek(other -> Board.resolveCollision(other, b));
        }).forEach(b -> board.checkHole(b));
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    

}
