package it.unibo.assignment01.controller;

import java.util.List;

import it.unibo.assignment01.model.Ball;
import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.CollisionPair;


public class CollisionTask implements Runnable{

    private Board board;
    private List<CollisionPair> collisionList;
    private Barrier barrier;

    public CollisionTask(List<CollisionPair> collisionList, Board board, Barrier barrier){
        this.board = board;
        this.collisionList = collisionList;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        collisionList.stream().forEach(c -> board.resolveCollision(c));
        try {
            barrier.hitAndWait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    

}
