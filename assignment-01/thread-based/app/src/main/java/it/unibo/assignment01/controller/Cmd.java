package it.unibo.assignment01.controller;

import it.unibo.assignment01.model.ball.Ball;

public interface Cmd {
	
	void execute(Ball ball);
}
