package it.unibo.assignment01.util;

public class SynchCell<T> {

	private T value;
	private boolean available;


	public SynchCell() {
		available = false;
	}

	public synchronized void set(T v) {
		while(available) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		value = v;
		available = true;
		notifyAll();
	}

	public synchronized T get() throws InterruptedException {
		while(!available) {	
			wait();
		}
		available = false;
		notifyAll();
		return value;
	}
}