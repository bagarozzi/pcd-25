package it.unibo.assignment01.controller;

import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.CollisionPair;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.Speed;


public class CollisionTask implements Runnable{

    private Board board;
    private List<Ball> collisionList;
    private Barrier barrier;

    public CollisionTask(List<Ball> collisionList, Board board, Barrier barrier){
        this.board = board;
        this.collisionList = collisionList;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        collisionList.stream().forEach(b-> board.getAllBall().stream().forEach(a ->  board.resolveCollision(new CollisionPair(a, b))));
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
