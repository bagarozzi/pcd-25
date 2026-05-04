package it.unibo.assignment01.controller;

public interface Controller extends Runnable {
    public void start();

    public void notifyCommand(Cmd cmd);
}
