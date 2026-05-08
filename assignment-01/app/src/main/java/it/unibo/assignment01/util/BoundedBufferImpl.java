package it.unibo.assignment01.util;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Parallel implementation of a bounded buffer
 * as a monitor, using reentrant locks
 * To make it parallel
 * @param <Item>
 */
public class BoundedBufferImpl<Item> implements BoundedBuffer<Item> {

	private final LinkedList<Item> buffer;
	private final int maxSize;

	private final Lock lock = new ReentrantLock();

	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();

	public BoundedBufferImpl(int size) {
		this.buffer = new LinkedList<Item>();
		this.maxSize = size;
	}

	@Override
	public void put(Item item) throws InterruptedException {
		lock.lock(); // Ingresso nel Monitor
		try {
			while (isFull()) {
				notFull.await(); // La GUI attende se il buffer è pieno
			}
			buffer.addLast(item);

			System.err.println("put-"+buffer.size());
			// Segnala al Controller che c'è un elemento da consumare
			notEmpty.signalAll();
		} finally {
			lock.unlock(); // Uscita dal Monitor
		}
	}

	@Override
	public Item get() throws InterruptedException {
		lock.lock(); // Ingresso nel Monitor
		try {
			while (isEmpty()) {
				notEmpty.await(); // Il Controller attende se il buffer è vuoto
			}
			Item item = buffer.removeFirst();
			System.err.println("get-"+buffer.size());
			// Segnala alla GUI che si è liberato un posto
			notFull.signalAll();
			return item;
		} finally {
			lock.unlock(); // Uscita dal Monitor
		}
	}

	public Optional<Item> lazyGet(){
		lock.lock();
		try {
			if(isEmpty()) {
				return Optional.empty();
			}
			Item item = buffer.removeFirst();
			return Optional.of(item);
		} finally {
			lock.unlock(); // Uscita dal Monitor
		}
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}
}