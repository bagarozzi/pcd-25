package it.unibo.assignment01.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchCell<T> {

	private T value;
	private boolean available;
	private Lock mutex;   
	private Condition isAvail;

	public SynchCell(){
		available = false;
		mutex = new ReentrantLock(); 
		isAvail = mutex.newCondition();
	}

	public void set(T v){
		try {
			mutex.lock();
			value = v;
			available = true;
			isAvail.signalAll();  
		} finally {
			mutex.unlock();
		}
	}
	
	public T get() {
		try {
			mutex.lock();
			if (!available){
				try {
					isAvail.await();
				} catch (InterruptedException ex){}
			} 
			available = false;
			return value;
		} finally {
			mutex.unlock();
		}
	}
}