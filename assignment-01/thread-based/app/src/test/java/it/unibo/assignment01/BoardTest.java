package it.unibo.assignment01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.unibo.assignment01.model.Board;
import it.unibo.assignment01.model.BoardImpl;
import it.unibo.assignment01.model.Position;
import it.unibo.assignment01.model.SimpleCollisionDetector;
import it.unibo.assignment01.model.Speed;
import it.unibo.assignment01.model.ball.Ball;
import it.unibo.assignment01.model.ball.BallImpl;

public class BoardTest {

    private static final double HOLE_RADUS = 0.2;
    private static final Position PLAYER_HOLE = new Position(-1.5, 1.0);
    private static final Position ENEMY_HOLE = new Position(1.5, 1.0);
    private static final Position BOARD_CENTER = new Position(0.0, 0.0);

    @Test
    public void testBallInsideHole() {
        List<Ball> ballList = new ArrayList<>();
        ballList.addAll(List.of(
            new BallImpl(PLAYER_HOLE, new Speed(0, 0), HOLE_RADUS, Ball.BALL_RADIUS),
            new BallImpl(ENEMY_HOLE, new Speed(0, 0), HOLE_RADUS, Ball.BALL_RADIUS)
        ));
        final Board board = new BoardImpl(ballList, new SimpleCollisionDetector());
        board.checkHole(ballList.get(0));
        board.checkHole(ballList.get(1));
        assertEquals(1, board.getHumanScore());
        assertEquals(1, board.getBotScore());
        assertTrue(board.getBalls().isEmpty());
    }

    @Test
    public void testBallOutsideHole() {
        List<Ball> ballList = new ArrayList<>();
        ballList.addAll(List.of(
            new BallImpl(PLAYER_HOLE, new Speed(0, 0), HOLE_RADUS, Ball.BALL_RADIUS),
            new BallImpl(ENEMY_HOLE, new Speed(0, 0), HOLE_RADUS, Ball.BALL_RADIUS)
        ));
        final Board board = new BoardImpl(ballList, new SimpleCollisionDetector());
        board.checkHole(new BallImpl(BOARD_CENTER, new Speed(0, 0), HOLE_RADUS, Ball.BALL_RADIUS));
        assertEquals(0, board.getHumanScore());
        assertEquals(0, board.getBotScore());
        assertEquals(ballList.size(), board.getBalls().size());
    }
}
