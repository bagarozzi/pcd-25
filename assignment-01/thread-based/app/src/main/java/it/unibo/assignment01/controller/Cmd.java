package it.unibo.assignment01.controller;

import it.unibo.assignment01.model.Ball;

public interface Cmd {
	
	void execute(Ball ball);
}
